package com.amotassic.dabaosword.api;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardPileInventory implements Container {
    public NonNullList<ItemStack> cards;
    public Player player;
    private static final Item pile = ModItems.CARD_PILE;

    public CardPileInventory(Player player) {
        this.player = player;
        this.cards = NonNullList.withSize(36, ItemStack.EMPTY);
        readNbt();
    }

    public int getEmptySlot() {
        for (int i = 0; i < getContainerSize(); ++i) {
            if (!cards.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public void readNbt() {
        cards.clear();
        try (var logging = new ProblemReporter.ScopedCollector(player.problemPath(), DabaoSword.LOGGER)) {
            var view = TagValueInput.create(logging, player.registryAccess(), getOrCreateNbt(trinketItem(pile, player)));
            ContainerHelper.loadAllItems(view, cards);
        }
    }

    public void writeNbtToStack() { //当涉及牌堆物品变化后，必须调用这个方法
        if (player.level().isClientSide()) return;
        ListTag nbtList;
        try (var logging = new ProblemReporter.ScopedCollector(player.problemPath(), DabaoSword.LOGGER)) {
            var view = TagValueOutput.createWithContext(logging, player.registryAccess());
            ContainerHelper.saveAllItems(view, cards);
            nbtList = view.buildResult().getList("Items").orElse(new ListTag());
        }
        ItemStack stack = trinketItem(pile, player);
        var nbtCompound = getOrCreateNbt(stack);
        nbtCompound.put("Items", nbtList);
        setNbt(stack, nbtCompound);
    }

    @Override
    public int getContainerSize() {return cards.size();}

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
    public @NonNull ItemStack getItem(int slot) {return cards.get(slot);}

    public int getSlotWith(ItemStack stack) { //倒序检索 3.3放弃了倒序检索，会出现bug
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.isEmpty()) continue;
            if (ItemStack.matches(itemStack, stack)) return i;
        }
        return -1;
    }

    /**@return 若成功移除指定物品则返回true，否则为false*/
    public boolean removeStack(ItemStack stack, int count) {
        int i = getSlotWith(stack);
        if (i == -1) return false;
        removeItem(i, count);
        return true;
    }

    @Override
    public @NonNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(cards, slot, amount);
        writeNbtToStack();
        return stack;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemStack = cards.get(slot);
        cards.set(slot, ItemStack.EMPTY);
        writeNbtToStack();
        return itemStack;
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack stack) {cards.set(slot, stack);}

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@NonNull Player player) {return true;}

    @Override
    public void clearContent() {
        cards.clear();
        writeNbtToStack();
    }

    public void insertStack(ItemStack stack) {
        if (insertStack(-1, stack)) writeNbtToStack();
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
            cards.set(slot, stack.copyAndClear());
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
        int i = stack.getCount();
        ItemStack itemStack = getItem(slot);
        if (itemStack.isEmpty()) {
            itemStack = stack.copyWithCount(0);
            setItem(slot, itemStack);
        }
        int j = getMaxStackSize(itemStack) - itemStack.getCount();
        int k = Math.min(i, j);
        if (k != 0) {
            i -= k;
            itemStack.grow(k);
        }
        return i;
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        for (int i = 0; i < cards.size(); ++i) {
            if (!canStackAddMore(cards.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.isSameItemSameComponents(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < this.getMaxStackSize(existingStack);
    }
}
