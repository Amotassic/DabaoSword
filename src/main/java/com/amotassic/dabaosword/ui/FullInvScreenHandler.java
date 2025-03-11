package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FullInvScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final LivingEntity target;
    private final boolean editable;

    public FullInvScreenHandler(int syncId, PlayerInventory playerInv, Inventory inventory, LivingEntity target) {
        super(ModItems.FULL_INV_SCREEN_HANDLER, syncId);
        this.inventory =inventory;
        this.target = target;
        this.editable = !inventory.getStack(61).isEmpty();
        int i;
        for (i = 0; i < 4; ++i) { //物品栏
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        for (i = 0; i < 5; ++i) addSlot(new Slot(inventory, i + 36, 170, 18 + i * 18)); //盔甲、副手
        for (i = 0; i < 2; ++i) { //饰品栏
            for (int j = 0; j < 10; ++j) {
                addSlot(new Slot(inventory, j + i * 10 + 41, 8 + j * 18, 108 + i * 18));
            }
        }
        if (playerInv.player != target) addPlayerInventorySlots(playerInv);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (editable) {
            super.onSlotClick(slotIndex, button, actionType, player);
            saveInv(inventory, target);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public static void saveInv(Inventory from, LivingEntity to) {
        for (int i = 0; i < 61; i++) {
            ItemStack stack = from.getStack(i);
            if (i == 0 && !(to instanceof PlayerEntity || to instanceof VillagerEntity)) to.setStackInHand(Hand.MAIN_HAND, stack);
            if (i < 8 && to instanceof VillagerEntity villager) villager.getInventory().setStack(i, stack);
            if (i < 36 && to instanceof PlayerEntity player)  player.getInventory().setStack(i, stack);
            if (i == 36) to.equipStack(EquipmentSlot.HEAD, stack);
            if (i == 37) to.equipStack(EquipmentSlot.CHEST, stack);
            if (i == 38) to.equipStack(EquipmentSlot.LEGS, stack);
            if (i == 39) to.equipStack(EquipmentSlot.FEET, stack);
            if (i == 40) to.setStackInHand(Hand.OFF_HAND, stack);
            if (i >= 41) {
                var pair = findSlot(to, i - 41);
                if (pair != null) pair.getFirst().getAccessories().setStack(pair.getSecond(), stack);
            }
        }
    }

    public static Pair<AccessoriesContainer, Integer> findSlot(LivingEntity entity, int index) {
        List<Pair<AccessoriesContainer, Integer>> slots = new ArrayList<>();
        AccessoriesCapability.getOptionally(entity).ifPresent(a -> a.getContainers().values().forEach(c -> c.getAccessories().forEach(p -> slots.add(new Pair<>(c, p.getFirst())))));
        for (var slot : slots) {if (slots.indexOf(slot) == index) return slot;}
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 17 + j * 18, 158 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 17 + i * 18, 216));
        }
    }
}
