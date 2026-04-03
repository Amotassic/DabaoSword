package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.SimplePayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.trinketsWithSlots;
import static net.minecraft.world.inventory.InventoryMenu.*;

public class FullInvScreenHandler extends AbstractContainerMenu {
    private final EntityInventory inventory;
    public final LivingEntity target;
    private final boolean editable;
    public final Set<Integer> slotsEnabled; //目标实体的可编辑槽位
    public final int rows; //总行数
    public final int armorRow; //盔甲栏以及副手物品所在的行
    public final boolean notSelf;
    private static final int TRINKET_INDEX = 43; //第一个饰品栏的索引

    public FullInvScreenHandler(int syncId, Inventory inv, SimplePayload buf) {
        super(ModItems.FULL_INV_SCREEN_HANDLER, syncId);
        this.target = (LivingEntity) inv.player.level().getEntity(Integer.parseInt(buf.name()));
        this.editable = Boolean.parseBoolean(buf.value());
        this.inventory = new EntityInventory(target, editable);
        this.slotsEnabled = inventory.slotIndexes;
        this.notSelf = inv.player != target;
        int row = 2, armor = 2; //行数和护甲栏所在行数
        int[] keySlots0 = new int[]{9, 18, 27};
        for (int i : keySlots0) {if (slotsEnabled.contains(i)) {armor++; row++;}}
        this.armorRow = armor;
        for (int i = TRINKET_INDEX; slotsEnabled.contains(i); i += 9) row++;
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
                new Object[]{36, 4, EMPTY_ARMOR_SLOT_HELMET},
                new Object[]{37, 5, EMPTY_ARMOR_SLOT_CHESTPLATE},
                new Object[]{38, 6, EMPTY_ARMOR_SLOT_LEGGINGS},
                new Object[]{39, 7, EMPTY_ARMOR_SLOT_BOOTS},
                new Object[]{40, 8, EMPTY_ARMOR_SLOT_SHIELD}
        ); //护甲和副手的格子
        for (Object[] data : slotData) {
            int slotId = (int) data[0]; int xOffset = (int) data[1];
            Identifier texture = (Identifier) data[2];
            addSlot(new Slot(inventory, slotId, 8 + xOffset * 18, 18 * armor) {
                public Identifier getNoItemIcon() {return texture;}
            });
        }
        addSlot(new Slot(inventory, TRINKET_INDEX - 2, 8 + 3 * 18, 18 * armor) {
            public Identifier getNoItemIcon() {return Identifier.withDefaultNamespace("container/slot/horse_armor");}
        }); // BodyArmor槽位
        addSlot(new Slot(inventory, TRINKET_INDEX - 1, 8 + 2 * 18, 18 * armor) {
            public Identifier getNoItemIcon() {return Identifier.withDefaultNamespace("container/slot/saddle");}
        }); // Saddle槽位

        outer: for (i = 0; i < 13; i++) { //饰品栏
            for (j = 0; j < 9; j++) {
                int index = TRINKET_INDEX + j + i * 9;
                if (!slotsEnabled.contains(index)) break outer;
                int x = 8 + j * 18; int y = (armor + 1 + i) * 18;
                addSlot(new Slot(inventory, index, x, y));
            }
        }
        if (notSelf) addPlayerInventorySlots(inv, rows);
    }

    @Override
    public void clicked(int slotIndex, int button, @NonNull ContainerInput actionType, @NonNull Player player) {
        if (editable) {
            if (button == 114 && !player.level().isClientSide()) {
                getSlot(slotIndex).set(ItemStack.EMPTY);
                return;
            }
            super.clicked(slotIndex, button, actionType, player);
        }
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override public boolean stillValid(@NonNull Player player) {return true;}

    private void addPlayerInventorySlots(Inventory inventory, int rows) {
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

    public static class EntityInventory implements Container {
        public final LivingEntity owner;
        public final Set<Integer> slotIndexes = new HashSet<>();

        public EntityInventory(LivingEntity owner, boolean editable) {
            this.owner = owner;
            // 如果是玩家，就加上36格物品栏，否则只加上主手
            if (owner instanceof Player && editable) for (int i = 0; i < 36; i++) slotIndexes.add(i);
            else slotIndexes.add(0);
            // 如果生物有物品栏，就加上物品栏的槽位，index为物品栏的index + 1
            if (owner instanceof InventoryCarrier inv) for (int i = 0; i < inv.getInventory().getContainerSize(); i++) slotIndexes.add(i + 1);
            // 如果是马之类的生物，添加它的物品栏
            if (owner instanceof AbstractHorse horse) for (int i = 0; i < horse.getInventorySize(); i++) slotIndexes.add(i + 1);
            // 所有生物都有盔甲和副手(40)
            int[] armorSlots = {36, 37, 38, 39, 40, 41, 42};
            for (int i : armorSlots) slotIndexes.add(i);
            // 添加所有饰品格子，index从43开始
            var trinkets = trinketsWithSlots(owner);
            for (int i = 0; i < trinkets.size(); i++) slotIndexes.add(TRINKET_INDEX + i);
        }

        @Override
        public int getContainerSize() {return slotIndexes.size();}

        /**尽量不要用，不过应该用不到吧*/
        @Override
        public boolean isEmpty() {
            for (int i : slotIndexes) if (!getItem(i).isEmpty()) return false;
            return true;
        }

        @Override
        public @NonNull ItemStack getItem(int slot) {
            if (slotIndexes.contains(slot)) {
                if (owner instanceof Player player) {
                    if (0 <= slot && slot <= 35) return player.getInventory().getItem(slot);
                } else if (slot == 0) return owner.getMainHandItem();
                if (0 < slot && slot <= 35) {
                    if (owner instanceof InventoryCarrier inv) return inv.getInventory().getItem(slot - 1);
                    if (owner instanceof AbstractHorse horse && slot <= horse.inventory.getContainerSize()) {
                        return horse.inventory.getItem(slot - 1);
                    }
                }
                if (35 < slot && slot < 40) return owner.getItemBySlot(EquipmentSlot.values()[41 - slot]);
                if (slot == 40) return owner.getOffhandItem();
                if (slot == 41) return owner.getItemBySlot(EquipmentSlot.BODY);
                if (slot == 42) return owner.getItemBySlot(EquipmentSlot.SADDLE);
                if (slot >= TRINKET_INDEX) {
                    var pair = trinketsWithSlots(owner).get(slot - TRINKET_INDEX);
                    return pair.getA().getItem(pair.getB());
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, @NonNull ItemStack stack) {
            if (!slotIndexes.contains(slot)) return;
            if (owner instanceof Player player) {
                if (0 <= slot && slot <= 35) player.getInventory().setItem(slot, stack);
            } else if (slot == 0) owner.setItemInHand(InteractionHand.MAIN_HAND, stack);
            if (0 < slot && slot <= 35) {
                if (owner instanceof InventoryCarrier inv) inv.getInventory().setItem(slot - 1, stack);
                if (owner instanceof AbstractHorse horse && slot <= horse.inventory.getContainerSize()) {
                    horse.inventory.setItem(slot - 1, stack);
                }
            }
            if (35 < slot && slot < 40) owner.setItemSlot(EquipmentSlot.values()[41 - slot], stack);
            if (slot == 40) owner.setItemInHand(InteractionHand.OFF_HAND, stack);
            if (slot == 41) owner.setItemSlot(EquipmentSlot.BODY, stack);
            if (slot == 42) owner.setItemSlot(EquipmentSlot.SADDLE, stack);
            if (slot >= TRINKET_INDEX) {
                var pair = trinketsWithSlots(owner).get(slot - TRINKET_INDEX);
                pair.getA().setItem(pair.getB(), stack);
            }
        }

        @Override
        public @NonNull ItemStack removeItem(int slot, int amount) {
            return getItem(slot).split(amount);
        }

        public @NonNull ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = getItem(slot);
            setItem(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public boolean stillValid(@NonNull Player player) {return true;}
        public void setChanged() {/*暂无操作*/}
        public void clearContent() {/*不许清空*/}
    }
}
