package com.amotassic.dabaosword.api;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class CardPileInventory implements Inventory {
    public DefaultedList<ItemStack> cards;
    public PlayerEntity player;
    public ItemStack pile;

    public CardPileInventory(PlayerEntity player) {
        this.player = player;
        this.pile = ModTools.trinketItem(ModItems.CARD_PILE, player);
        this.cards = DefaultedList.ofSize(36, ItemStack.EMPTY);
        readNbt();
    }

    public int getEmptySlot() {
        for (int i = 0; i < size(); ++i) {
            if (!cards.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public void readNbt() {
        if (pile.getNbt() != null) {
            NbtList list = (NbtList) pile.getNbt().get("Items");
            if (list != null) readNbt(list);
        }
    }

    public void readNbt(NbtList nbtList) {
        cards.clear();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot");
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (itemStack.isEmpty()) continue;
            if (j >= 0 && j < size()) setStack(j, itemStack);
        }
    }

    public void writeNbtToStack() { //当涉及牌堆物品变化后，必须调用这个方法
        NbtList nbtList = new NbtList();
        NbtCompound nbtCompound;
        for (int i = 0; i < size(); ++i) {
            if (cards.get(i).isEmpty()) continue;
            nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte) i);
            cards.get(i).writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        NbtCompound nbt = pile.getOrCreateNbt();
        nbt.put("Items", nbtList);
        pile.setNbt(nbt);
    }

    @Override
    public int size() {return cards.size();}

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : cards) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public boolean isNotFull() {
        for (ItemStack itemStack : cards) {
            if (itemStack.isEmpty()) return true;
        }
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {return cards.get(slot);}

    public int getSlotWith(ItemStack stack) { //倒序检索
        for (int i = size() - 1; i >= 0; i--) {
            ItemStack itemStack = getStack(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.equals(stack)) return i;
        }
        return -1;
    }

    public void removeStack(ItemStack stack, int count) {
        int i = getSlotWith(stack);
        if (i == -1) return;
        removeStack(i, count);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(cards, slot, amount);
        writeNbtToStack();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack = cards.get(slot);
        cards.set(slot, ItemStack.EMPTY);
        writeNbtToStack();
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {cards.set(slot, stack);}

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {return true;}

    @Override
    public void clear() {
        cards.clear();
        writeNbtToStack();
    }

    public void insertStack(ItemStack stack) {
        if (insertStack(-1, stack)) writeNbtToStack();;
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.isDamaged()) {
            int i;
            do {
                i = stack.getCount();
                if (slot == -1) {
                    stack.setCount(addStack(stack));
                    continue;
                }
                stack.setCount(addStack(slot, stack));
            } while (!stack.isEmpty() && stack.getCount() < i);
            return stack.getCount() < i;
        }
        if (slot == -1) slot = getEmptySlot();
        if (slot >= 0) {
            cards.set(slot, stack.copyAndEmpty());
            return true;
        }
        return false;
    }

    private int addStack(ItemStack stack) {
        int i = getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) i = getEmptySlot();
        if (i == -1) return stack.getCount();
        return addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        int j;
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasNbt()) itemStack.setNbt(stack.getNbt().copy());
            setStack(slot, itemStack);
        }
        if ((j = i) > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }
        if (j > getMaxCountPerStack() - itemStack.getCount()) {
            j = getMaxCountPerStack() - itemStack.getCount();
        }
        if (j == 0) return i;
        itemStack.increment(j);
        return i - j;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        for (int i = 0; i < cards.size(); ++i) {
            if (!canStackAddMore(cards.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.canCombine(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < getMaxCountPerStack();
    }
}
