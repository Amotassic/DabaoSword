package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final ItemStack stack;
    private final int cards;
    private final boolean isPlayerInv;
    private final Skill skill;
    private final List<Integer> clicks = new ArrayList<>();
    @Nullable private PlayerEntity invOwner;

    public PlayerInvScreenHandler(int syncId, Inventory inventory, PlayerEntity target) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.cards = inventory.getStack(54).getCount();
        this.stack = inventory.getStack(55);
        this.isPlayerInv = !inventory.getStack(56).isEmpty();
        this.skill = s(stack);
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
        inventory.setStack(57, paibei());
        for (int i = 0; i < 4; i++) addSlot(new Slot(inventory, 54 + i, 114514, 114514));
    }

    @Override
    public void onSlotClick(int index, int button, SlotActionType action, PlayerEntity player) {
        if (index >= 0 && index < 54 && !player.getWorld().isClient) {
            if (invOwner == null) invOwner = isPlayerInv ? player : target;
            //System.out.println(index + " button: " + button + " action: " + action);
            var selectedStack = getStack(index);
            if (button == 65 && maxSelect() >= 100) forEachNonEmptySlot(s -> addClick(s.getIndex(), 99)); //全选
            if (button == 90) forEachNonEmptySlot(s -> setClick(s.getIndex(), 0)); //清空
            if (!selectedStack.isEmpty()) skill.item.onSlotClick(this, player, skill, target, index, button, action);
            writeClicks();

            if (stack.isOf(ModItems.WANJIAN)) {
                ItemStack mainHand = player.getMainHandStack(); var mainCopy = mainHand.copy();
                var cards = getCardPack(player);
                ItemStack selected = ItemStack.EMPTY; //对选择的卡牌进行赋值
                if (index == 8) selected = player.getOffHandStack();
                if (8 < index && index < 45) selected = cards.getStack(index - 9);
                if (index >= 45) selected = selectedStack;
                var copy = selected.copy(); //复制一份已选物品方便代码操作

                if (!selected.isEmpty()) {
                    if (isCard(mainHand)) { //如果主手物品是卡牌，就把主手物品设置为选择的牌，然后主手物品进入牌堆背包
                        player.setStackInHand(Hand.MAIN_HAND, copy);
                        selected.setCount(0);
                        cards.insertStack(mainCopy);
                    } else {
                        if (index == 8 || index >= 45) { //如果选的不是牌堆中的牌，交换两者位置
                            player.setStackInHand(Hand.MAIN_HAND, copy);
                            selected.setCount(0);
                            if (index == 8) player.setStackInHand(Hand.OFF_HAND, mainCopy); //处理副手
                            if (index >= 45) player.getInventory().setStack(index - 45, mainCopy);
                        } else {
                            int emptySlot = player.getInventory().getEmptySlot();
                            if (emptySlot == -1) player.sendMessage(Text.translatable("card_pile.player_inv.full").formatted(Formatting.RED), true);
                            else { //如果选择牌堆中的牌且背包未满，则将主手物品设为选择的牌，主手物品移动到其它空槽位（显然不包括副手）
                                player.setStackInHand(Hand.MAIN_HAND, copy);
                                cards.removeStack(index - 9);
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
                if (stack.isOf(ModItems.STEAL)) {
                    Text message = Text.translatable("dabaosword.steal", player.getDisplayName(), target.getDisplayName(), selectedStack.toHoverableText());
                    player.sendMessage(message, false);
                    target.sendMessage(message, false);
                    if (isCard(selectedStack)) { //如果选择的物品是卡牌才触发事件
                        var exData = d().cards(selectedStack, 1, index < 4);
                        CardEvents.cardMove(target, exData, player);
                    } else {
                        give(player, selectedStack.copyWithCount(1)); /*顺手：复制一个物品*/
                        selectedStack.decrement(1);
                    }
                    closeGUI(player);
                }

                if (stack.isOf(ModItems.DISCARD)) {
                    Text message = Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selectedStack.toHoverableText());
                    player.sendMessage(message, false);
                    target.sendMessage(message, false);
                    var exData = d().cards(selectedStack, 1, index < 4);
                    cardDiscard(target, exData);
                    closeGUI(player);
                }
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        skill.item.onGuiClose(this, player, skill, target);
    }

    /**获取该容器内对应slot上的物品，即使卡牌以牌背形态显示，也返回目标对应的卡牌*/
    public ItemStack getStack(int slotIndex) {
        if (slotIndex < 0 || slotIndex > 53) return ItemStack.EMPTY;
        var item = getSlot(slotIndex).getStack();
        if (cards != 1) return item;
        if (invOwner != null && item.isOf(ModItems.GAIN_CARD)) {
            var pack = getCardPack(invOwner); var main = invOwner.getInventory().main;
            if (pack.isEmpty()) return main.get(slotIndex - 9);
            else {
                if (slotIndex < 45) return pack.getStack(slotIndex - 9);
                else return main.get(slotIndex - 45);
            }
        }
        return item;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        var effect = player.getStatusEffect(ModItems.COOLDOWN2);
        return effect == null || effect.getAmplifier() != 2;
    }

    public void forEachNonEmptySlot(Consumer<Slot> action) {
        for (int i = 0; i < 54; i++) {
            Slot slot = getSlot(i);
            if (slot.hasStack()) action.accept(slot);
        }
    }

    private void writeClicks() {
        ItemStack cinfo = getSlot(57).getStack();
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Clicks", getClickMap().toString());
        setNbt(cinfo, nbt);
    }

    public int maxSelect() {return skill.getMaxSelect();}

    private int getSlotClicked(int slot) {
        if (!clicks.contains(slot)) return 0;
        return getClickMap().get(slot);
    }

    /**使点击的槽位物品选择数量增加，如果增加后已选择物品数量大于最大可选数量，会自动移除最早的选择项*/
    public void addClick(int slot, int... count) {
        int num = count.length > 0 ? count[0] : 1;
        int n = getSlot(slot).getStack().getCount();
        num = Math.min(n - getSlotClicked(slot), num);
        for (int i = 0; i < num; i++) {
            clicks.add(slot);
            if (getSelectedCount() > maxSelect()) dropFirst();
        }
    }

    /**使点击的槽位物品选择数量减少*/
    public void dropClick(int slot, int... count) {
        if (!clicks.contains(slot)) return;
        int num = count.length > 0 ? count[0] : 1;
        for (int i = 0; i < num; i++) {
            if (!clicks.remove(Integer.valueOf(slot))) break;
        }
    }

    /**设置该槽位的选择数*/
    public void setClick(int slot, int count) {
        if (clicks.contains(slot)) dropClick(slot, 114514);
        if (count > 0) addClick(slot, count);
    }

    public void dropFirst(int... count) {
        if (clicks.isEmpty()) return;
        dropClick(clicks.getFirst(), count);
    }

    /**@return GUI中已选择卡牌的数量*/
    public int getSelectedCount() {return clicks.size();}

    public List<ItemStack> getSelected() {
        List<ItemStack> selected = new ArrayList<>();
        clicks.forEach(slot -> {
            ItemStack stack = getSlot(slot).getStack();
            if (!stack.isEmpty()) selected.add(stack.copyWithCount(1));
        });
        return selected;
    }

    public Map<Integer, Integer> getClickMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Integer num : clicks) {
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else map.put(num, 1);
        }
        return map;
    }

    /**将GUI中已选的卡牌直接输出为ExData，省去手动获取已选卡牌的功夫，一般用于{@link com.amotassic.dabaosword.api.skill.ISkill#onGuiClose(PlayerInvScreenHandler, PlayerEntity, Skill, PlayerEntity)}*/
    public ExData toExData() {
        var exData = d(); var clickMap = getClickMap();
        clickMap.forEach((slot, count) -> {
            ItemStack stack = getStack(slot);
            if (isCard(stack)) exData.cards(stack, count, slot < 4);
        });
        return exData;
    }

}
