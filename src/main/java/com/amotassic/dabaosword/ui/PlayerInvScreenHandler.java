package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.amotassic.dabaosword.api.event.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final ItemStack stack;
    private final int cards;
    private final boolean isPlayerInv;

    public PlayerInvScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, new SimpleInventory(60), inv.player.getWorld().getPlayerByUuid(buf.readUuid()));
    }

    public PlayerInvScreenHandler(int syncId, Inventory inventory, PlayerEntity target) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.cards = inventory.getStack(54).getCount();
        this.stack = inventory.getStack(55);
        this.isPlayerInv = !inventory.getStack(56).isEmpty();
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 54 && !player.getWorld().isClient) {
            var selectedStack = selected(isPlayerInv ? player : target, slotIndex);

            if (stack.isOf(ModItems.WANJIAN)) {
                ItemStack mainHand = player.getMainHandStack(); var mainCopy = mainHand.copy();
                CardPileInventory cards = new CardPileInventory(player);
                ItemStack selected = ItemStack.EMPTY; //对选择的卡牌进行赋值
                if (slotIndex == 8) selected = player.getOffHandStack();
                if (8 < slotIndex && slotIndex < 45) selected = cards.getStack(slotIndex - 9);
                if (slotIndex >= 45) selected = selectedStack;
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
                if (selectedStack.isEmpty()) { //如果玩家点了一个空的格子————
                    int emptySlot = player.getInventory().getEmptySlot();
                    if (emptySlot != -1) { //如果主手不为空，就把主手的物品移动到其他空格子，主手设为空
                        player.getInventory().setStack(emptySlot, mainHand.copy());
                        mainHand.setCount(0);
                    }
                } else { //如果玩家选了一个非空的格子，就交换主手和该格子的物品
                    ItemStack mainCopy = mainHand.copy(); ItemStack swapCopy = selectedStack.copy();
                    if (player.getOffHandStack().equals(selectedStack)) {
                        player.setStackInHand(Hand.MAIN_HAND, swapCopy);
                        player.setStackInHand(Hand.OFF_HAND, mainCopy);
                    } else {
                        int swapSlot = player.getInventory().getSlotWithStack(selectedStack);
                        player.setStackInHand(Hand.MAIN_HAND, swapCopy);
                        player.getInventory().setStack(swapSlot, mainCopy);
                    }
                }
                closeGUI(player);
            }

            if (!selectedStack.isEmpty()) {
                if (stack.getItem() instanceof Skill skill) skill.onClickGUISlot(player, stack, target, selectedStack, slotIndex);

                if (stack.isOf(ModItems.STEAL)) {
                    Text message = Text.translatable("dabaosword.steal", player.getDisplayName(), target.getDisplayName(), selectedStack.toHoverableText());
                    player.sendMessage(message);
                    target.sendMessage(message);
                    if (isCard(selectedStack)) cardMove(target, player, selectedStack, 1, slotIndex < 4, false);
                        //如果选择的物品是卡牌才触发事件
                    else {give(player, selectedStack.copyWithCount(1)); /*顺手：复制一个物品*/
                        selectedStack.decrement(1);}
                    cardUsePost(player, stack, target);
                    closeGUI(player);
                }

                if (stack.isOf(ModItems.DISCARD)) {
                    Text message = Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selectedStack.toHoverableText());
                    player.sendMessage(message);
                    target.sendMessage(message);
                    cardDiscard(target, selectedStack, 1, slotIndex < 4);
                    cardUsePost(player, stack, target);
                    closeGUI(player);
                }
            }
        }
    }

    private ItemStack selected(PlayerEntity player, int slotIndex) {
        var itemStack = getSlot(slotIndex).getStack();
        if (itemStack.isEmpty() && cards == 1 && slotIndex >= 8) {
            List<ItemStack> candidate = getItems(player, isCard, true, false, false, true);
            if(!candidate.isEmpty()) return candidate.get(new Random().nextInt(candidate.size()));
        }
        return itemStack;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }

}
