package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.event.callback.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final ItemStack eventStack;
    private final int cards;

    public PlayerInvScreenHandler(int syncId, Inventory inventory, PlayerEntity target) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.cards = inventory.getStack(54).getCount();
        this.eventStack = inventory.getStack(55);
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 54) {
            var stack = getStack(player, eventStack);
            var targetStack = selected(target, slotIndex); //根据情况来判断需要选择自己的stack还是目标的stack
            var selfStack = selected(player, slotIndex);
            if (selfStack != ItemStack.EMPTY) {

                if (stack.getItem() == SkillCards.RENDE) {
                    voice(player, Sounds.RENDE);
                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
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
                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    player.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("give_card.tip", stack.toHoverableText(), target.getDisplayName())));
                    cardMove(player, target, selfStack, 1, CardCBs.T.INV_TO_INV);
                    setTag(stack, i - 1);
                    if (i - 1 == 0) closeGUI(player);
                }
            }

            if (targetStack != ItemStack.EMPTY) {

                if (stack.getItem() == SkillCards.SHANZHUAN) {
                    voice(player, Sounds.SHANZHUAN);
                    if (targetStack.isIn(Tags.Items.BASIC_CARD)) target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                    else target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(targetStack.toHoverableText()));
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
                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("dabaosword.steal")).append(targetStack.toHoverableText()));
                    CardCBs.T type = slotIndex < 4 ? CardCBs.T.EQUIP_TO_INV : CardCBs.T.INV_TO_INV;
                    if (isCard(targetStack)) cardMove(target, player, targetStack, 1, type);
                    //如果选择的物品是卡牌才触发事件
                    else {give(player, targetStack.copyWithCount(1)); /*顺手：复制一个物品*/
                        targetStack.decrement(1);}
                    cardUsePost(player, stack, target);
                    closeGUI(player);
                }

                if (stack.getItem() == ModItems.DISCARD) {
                    voice(player, Sounds.GUOHE);
                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(targetStack.toHoverableText()));
                    cardDiscard(target, targetStack, 1, slotIndex < 4);
                    cardUsePost(player, stack, target);
                    closeGUI(player);
                }
            }
        }
    }

    private ItemStack selected(PlayerEntity player, int slotIndex) {
        var itemStack = getSlot(slotIndex).getStack();
        if (!itemStack.isEmpty()) {
            if (slotIndex < 4) {
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack = entry.getRight();
                        if (itemStack.equals(stack)) return stack;
                    }
                }
            }
            if (slotIndex > 3 && slotIndex < 8) {
                for (ItemStack stack : player.getArmorItems()) {
                    if (itemStack.equals(stack)) return stack;
                }
            }
            if (slotIndex >= 8) {
                DefaultedList<ItemStack> inventory = player.getInventory().main;
                for (ItemStack stack : inventory) {
                    if (itemStack.equals(stack)) return stack;
                }
                if (player.getOffHandStack().equals(itemStack)) return player.getOffHandStack();
            }
        } else if (cards == 1 && slotIndex >= 8) {
            List<ItemStack> candidate = new ArrayList<>();
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
        return ItemStack.EMPTY;
    }

    private ItemStack getStack(PlayerEntity player, ItemStack eventStack) { //获取原本的stack
        List<ItemStack> candidate = new ArrayList<>();
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if(component.isPresent()) {
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                candidate.add(entry.getRight());
            }
        }
        candidate.addAll(player.getInventory().main);
        for (ItemStack stack : candidate) {
            if (stack.equals(eventStack)) return stack;
        }
        return ItemStack.EMPTY;
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
