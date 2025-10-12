package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.trinketsWithSlots;
import static net.minecraft.screen.PlayerScreenHandler.*;

public class FullInvScreenHandler extends ScreenHandler {
    private final EntityInventory inventory;
    public final LivingEntity target;
    private final boolean editable;
    public final Set<Integer> slotsEnabled; //目标实体的可编辑槽位
    public final int rows; //总行数
    public final int armorRow; //盔甲栏以及副手物品所在的行
    public final boolean notSelf;

    public FullInvScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        super(ModItems.FULL_INV_SCREEN_HANDLER, syncId);
        this.target = (LivingEntity) inv.player.getWorld().getEntityById(buf.readInt());
        this.editable = buf.readBoolean();
        this.inventory = new EntityInventory(target, editable);
        this.slotsEnabled = inventory.slotIndexes;
        this.notSelf = inv.player != target;
        int row = 2, armor = 2; //行数和护甲栏所在行数
        int[] keySlots0 = new int[]{9, 18, 27};
        for (int i : keySlots0) {if (slotsEnabled.contains(i)) {armor++; row++;}}
        this.armorRow = armor;
        for (int i = 42; slotsEnabled.contains(i); i += 9) row++;
        this.rows = row;
        int i, j;
        for (i = 0; i < 4; i++) { //物品栏，即使没有启用，也需要添加槽位，否则会导致崩溃
            for (j = 0; j < 9; j++) {
                int index = j + i * 9; boolean enabled = slotsEnabled.contains(index);
                int x = enabled ? 8 + j * 18 : 114514; int y = enabled ? 18 + i * 18 : 114514;
                addSlot(new Slot(inventory, index, x, y) {public boolean canInsert(ItemStack stack) {return enabled;}});
            }
        }
        List<Object[]> slotData = Arrays.asList(
                new Object[]{36, 4, EMPTY_HELMET_SLOT_TEXTURE},
                new Object[]{37, 5, EMPTY_CHESTPLATE_SLOT_TEXTURE},
                new Object[]{38, 6, EMPTY_LEGGINGS_SLOT_TEXTURE},
                new Object[]{39, 7, EMPTY_BOOTS_SLOT_TEXTURE},
                new Object[]{40, 8, EMPTY_OFFHAND_ARMOR_SLOT}
        ); //护甲和副手的格子
        for (Object[] data : slotData) {
            int slotId = (int) data[0]; int xOffset = (int) data[1];
            Identifier texture = (Identifier) data[2];
            addSlot(new Slot(inventory, slotId, 8 + xOffset * 18, 18 * armor) {
                public Pair<Identifier, Identifier> getBackgroundSprite() {return Pair.of(BLOCK_ATLAS_TEXTURE, texture);}
            });
        }
        // BodyArmor槽位，1.20没有，为了保持跟1.21代码统一，留空
        addSlot(new Slot(inventory, 41, 114514, 114514) {public boolean canInsert(ItemStack stack) {return false;}});

        outer: for (i = 0; i < 13; i++) { //饰品栏
            for (j = 0; j < 9; j++) {
                int index = 42 + j + i * 9;
                if (!slotsEnabled.contains(index)) break outer;
                int x = 8 + j * 18; int y = (armor + 1 + i) * 18;
                addSlot(new Slot(inventory, index, x, y));
            }
        }
        if (notSelf) addPlayerInventorySlots(inv, rows);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (editable) {
            if (button == 261 && !player.getWorld().isClient) {
                getSlot(slotIndex).setStack(ItemStack.EMPTY);
                return;
            }
            super.onSlotClick(slotIndex, button, actionType, player);
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

    @Override public boolean canUse(PlayerEntity player) {return true;}

    private void addPlayerInventorySlots(PlayerInventory inventory, int rows) {
        int i, j;
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 32 + 18 * (rows + i)));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 36 + 18 * (rows + 3)));
        }
    }

    public static class EntityInventory implements Inventory {
        public final LivingEntity owner;
        public final Set<Integer> slotIndexes = new HashSet<>();

        public EntityInventory(LivingEntity owner, boolean editable) {
            this.owner = owner;
            // 如果是玩家，就加上36格物品栏，否则只加上主手
            if (owner instanceof PlayerEntity && editable) for (int i = 0; i < 36; i++) slotIndexes.add(i);
            else slotIndexes.add(0);
            // 如果生物有物品栏，就加上物品栏的槽位，index为物品栏的index + 1
            if (owner instanceof InventoryOwner inv) for (int i = 0; i < inv.getInventory().size(); i++) slotIndexes.add(i + 1);
            // 如果是马之类的生物，添加它的物品栏
            if (owner instanceof AbstractHorseEntity horse) for (int i = 0; i < horse.getInventorySize(); i++) slotIndexes.add(i + 1);
            // 所有生物都有盔甲和副手(40)
            int[] armorSlots = {36, 37, 38, 39, 40};
            for (int i : armorSlots) slotIndexes.add(i);
            // 添加所有饰品格子，index从42开始
            var trinkets = trinketsWithSlots(owner);
            for (int i = 0; i < trinkets.size(); i++) slotIndexes.add(42 + i);
        }

        @Override
        public int size() {return slotIndexes.size();}

        /**尽量不要用，不过应该用不到吧*/
        @Override
        public boolean isEmpty() {
            for (int i : slotIndexes) if (!getStack(i).isEmpty()) return false;
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            if (slotIndexes.contains(slot)) {
                if (owner instanceof PlayerEntity player) {
                    if (0 <= slot && slot <= 35) return player.getInventory().getStack(slot);
                } else if (slot == 0) return owner.getMainHandStack();
                if (0 < slot && slot <= 35) {
                    if (owner instanceof InventoryOwner inv) return inv.getInventory().getStack(slot - 1);
                    if (owner instanceof AbstractHorseEntity horse && slot <= horse.items.size()) {
                        return horse.items.getStack(slot - 1);
                    }
                }
                if (35 < slot && slot < 40) return owner.getEquippedStack(EquipmentSlot.values()[41 - slot]);
                if (slot == 40) return owner.getOffHandStack();
                if (slot >= 42) {
                    var pair = trinketsWithSlots(owner).get(slot - 42);
                    return pair.getLeft().getStack(pair.getRight());
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            if (!slotIndexes.contains(slot)) return;
            if (owner instanceof PlayerEntity player) {
                if (0 <= slot && slot <= 35) player.getInventory().setStack(slot, stack);
            } else if (slot == 0) owner.setStackInHand(Hand.MAIN_HAND, stack);
            if (0 < slot && slot <= 35) {
                if (owner instanceof InventoryOwner inv) inv.getInventory().setStack(slot - 1, stack);
                if (owner instanceof AbstractHorseEntity horse && slot <= horse.items.size()) {
                    horse.items.setStack(slot - 1, stack);
                }
            }
            if (35 < slot && slot < 40) owner.equipStack(EquipmentSlot.values()[41 - slot], stack);
            if (slot == 40) owner.setStackInHand(Hand.OFF_HAND, stack);
            if (slot >= 42) {
                var pair = trinketsWithSlots(owner).get(slot - 42);
                pair.getLeft().setStack(pair.getRight(), stack);
            }
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return getStack(slot).split(amount);
        }

        @Override
        public ItemStack removeStack(int slot) {
            ItemStack stack = getStack(slot);
            setStack(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {return true;}
        public void markDirty() {/*暂无操作*/}
        public void clear() {/*不许清空*/}
    }
}
