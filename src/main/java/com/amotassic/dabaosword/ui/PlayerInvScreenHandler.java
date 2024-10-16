package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final ItemStack stack;
    private final int cards;

    public PlayerInvScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, new SimpleInventory(60), inv.player.getWorld().getPlayerByUuid(buf.readUuid()), buf.readItemStack());
    }

    public PlayerInvScreenHandler(int syncId, Inventory inventory, PlayerEntity target, ItemStack stack) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.stack = stack;
        this.cards = inventory.getStack(54).getCount();
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 54 && !player.getWorld().isClient) {
            var targetStack = selected(target, slotIndex); //根据情况来判断需要选择自己的stack还是目标的stack
            var selfStack = selected(player, slotIndex);

            if (stack.isOf(ModItems.WANJIAN)) {
                ItemStack mainHand = player.getMainHandStack(); var mainCopy = mainHand.copy();
                CardPileInventory cards = new CardPileInventory(player);
                ItemStack selected = ItemStack.EMPTY; //对选择的卡牌进行赋值
                if (slotIndex == 8) selected = player.getOffHandStack();
                if (8 < slotIndex && slotIndex < 45) selected = cards.getStack(slotIndex - 9);
                if (slotIndex >= 45) selected = selfStack;
                var copy = selected.copy(); //复制一份已选物品方便代码操作

                if (!selected.isEmpty()) {
                    if (isCard(mainHand)) { //如果主手物品是卡牌，就把主手物品设置为选择的牌，然后主手物品进入牌堆背包
                        player.setStackInHand(Hand.MAIN_HAND, copy);
                        selected.setCount(0);
                        cards.insertStack(mainCopy);
                    } else {
                        if (slotIndex == 8 || slotIndex >= 45) { //如果选的不是牌堆中的牌，交换两者位置
                            player.setStackInHand(Hand.MAIN_HAND, copy);
                            selected.setCount(0);
                            if (slotIndex == 8) player.setStackInHand(Hand.OFF_HAND, mainCopy); //处理副手
                            if (slotIndex >= 45) player.getInventory().setStack(slotIndex - 45, mainCopy);
                        } else {
                            int emptySlot = player.getInventory().getEmptySlot();
                            if (emptySlot == -1) player.sendMessage(Text.translatable("card_pile.player_inv.full").formatted(Formatting.RED), true);
                            else { //如果选择牌堆中的牌且背包未满，则将主手物品设为选择的牌，主手物品移动到其它空槽位（显然不包括副手）
                                player.setStackInHand(Hand.MAIN_HAND, copy);
                                cards.removeStack(slotIndex - 9);
                                //如果主手原本是空的，就不需要交换这一步，这点很重要
                                if (!mainCopy.isEmpty()) player.getInventory().setStack(emptySlot, mainCopy);
                            }
                        }
                    }
                }
                closeGUI(player);
            }

            if (stack.isOf(ModItems.SUNSHINE_SMILE)) {
                ItemStack mainHand = player.getMainHandStack();
                if (selfStack.isEmpty()) { //如果玩家点了一个空的格子————
                    int emptySlot = player.getInventory().getEmptySlot();
                    if (emptySlot != -1) { //如果主手不为空，就把主手的物品移动到其他空格子，主手设为空
                        player.getInventory().setStack(emptySlot, mainHand.copy());
                        mainHand.setCount(0);
                    }
                } else { //如果玩家选了一个非空的格子，就交换主手和该格子的物品
                    ItemStack mainCopy = mainHand.copy(); ItemStack swapCopy = selfStack.copy();
                    if (player.getOffHandStack().equals(selfStack)) {
                        player.setStackInHand(Hand.MAIN_HAND, swapCopy);
                        player.setStackInHand(Hand.OFF_HAND, mainCopy);
                    } else {
                        int swapSlot = player.getInventory().getSlotWithStack(selfStack);
                        player.setStackInHand(Hand.MAIN_HAND, swapCopy);
                        player.getInventory().setStack(swapSlot, mainCopy);
                    }
                }
                closeGUI(player);
            }

            if (!selfStack.isEmpty()) {

                if (stack.getItem() == SkillCards.RENDE) {
                    voice(player, Sounds.RENDE);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    cardMove(player, target, selfStack, 1, CardCBs.T.INV_TO_INV);
                    int cd = getCD(stack);
                    if (player.getHealth() < player.getMaxHealth() && cd == 0 && new Random().nextFloat() < 0.5) {
                        player.heal(5); voice(player, Sounds.RECOVER);
                        player.sendMessage(Text.translatable("recover.tip").formatted(Formatting.GREEN), true);
                        setCD(stack, 30);
                    }
                }

                if (stack.getItem() == SkillCards.YIJI) {
                    int i = getTag(stack);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    cardMove(player, target, selfStack, 1, CardCBs.T.INV_TO_INV);
                    setTag(stack, i - 1);
                    if (i - 1 == 0) closeGUI(player);
                }
            }

            if (!targetStack.isEmpty()) {

                if (stack.getItem() == SkillCards.SHANZHUAN) {
                    voice(player, Sounds.SHANZHUAN);
                    if (targetStack.isIn(Tags.Items.BASIC_CARD)) target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                    else target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.discard")).append(targetStack.toHoverableText()));
                    cardDiscard(target, targetStack, 1, slotIndex < 4);
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
                    closeGUI(player);
                }

                if (stack.getItem() == SkillCards.GONGXIN) {
                    cardDiscard(target, targetStack, 1, false);
                    closeGUI(player);
                }

                if (stack.getItem() == SkillCards.ZHIHENG) {
                    int z = getTag(stack);
                    voice(player, Sounds.ZHIHENG);
                    cardDiscard(player, targetStack, 1, slotIndex < 4);
                    if (new Random().nextFloat() < 0.1) {
                        draw(player, 2);
                        player.sendMessage(Text.translatable("zhiheng.extra").formatted(Formatting.GREEN), true);
                    } else draw(player);
                    setTag(stack, z - 1);
                    if (z - 1 == 0) closeGUI(player);
                }

                if (stack.getItem() == ModItems.STEAL) {
                    voice(player, Sounds.SHUNSHOU);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.steal")).append(targetStack.toHoverableText()));
                    CardCBs.T type = slotIndex < 4 ? CardCBs.T.EQUIP_TO_INV : CardCBs.T.INV_TO_INV;
                    if (isCard(targetStack)) cardMove(target, player, targetStack, 1, type);
                    //如果选择的物品是卡牌才触发事件
                    else {give(player, targetStack.copyWithCount(1)); /*顺手：复制一个物品*/
                        targetStack.decrement(1);}
                    nonPreUseCardDecrement(player, stack, target);
                    closeGUI(player);
                }

                if (stack.getItem() == ModItems.DISCARD) {
                    voice(player, Sounds.GUOHE);
                    target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.discard")).append(targetStack.toHoverableText()));
                    cardDiscard(target, targetStack, 1, slotIndex < 4);
                    nonPreUseCardDecrement(player, stack, target);
                    closeGUI(player);
                }
            }
        }
    }

    private ItemStack selected(PlayerEntity player, int slotIndex) {
        var itemStack = getSlot(slotIndex).getStack();
        if (itemStack.isEmpty() && cards == 1 && slotIndex >= 8) {
            List<ItemStack> candidate = new ArrayList<>(new CardPileInventory(player).nonEmpty);
            DefaultedList<ItemStack> inventory = player.getInventory().main;
            List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(i -> isCard(inventory.get(i))).boxed().toList();
            for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
            ItemStack off = player.getOffHandStack();
            if (isCard(off)) candidate.add(off);
            if(!candidate.isEmpty()) {
                int index = new Random().nextInt(candidate.size());
                return candidate.get(index);
            }
        }
        return itemStack;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }

    private void closeGUI(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }

}
