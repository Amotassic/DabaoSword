package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.OpenScreenPayload;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.screen.PlayerScreenHandler.*;

public class FullInvScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final LivingEntity target;
    private final boolean editable;
    public final Set<Integer> slotsEnabled; //目标实体的可编辑槽位
    public final int rows; //总行数
    public final int armorRow; //盔甲栏以及副手物品所在的行
    public final boolean notSelf;

    public FullInvScreenHandler(int syncId, PlayerInventory inv, OpenScreenPayload buf) {
        super(ModItems.FULL_INV_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(86);
        this.target = (LivingEntity) inv.player.getWorld().getEntityById(buf.id());
        this.editable = buf.bl();
        this.slotsEnabled = setup(inventory, target, editable);
        this.notSelf = inv.player != target;
        int row = 2, armor = 2; //行数和护甲栏所在行数
        int[] keySlots0 = new int[]{9, 18, 27};
        for (int i : keySlots0) {if (slotsEnabled.contains(i)) armor++;}
        this.armorRow = armor;
        int[] keySlots = new int[]{9, 18, 27, 41, 50, 59, 68, 77};
        for (int i : keySlots) {if (slotsEnabled.contains(i)) row++;}
        this.rows = row;
        int i, j;
        for (i = 0; i < 4; i++) { //物品栏
            for (j = 0; j < 9; j++) {
                int index = j + i * 9; boolean enabled = slotsEnabled.contains(index);
                int x = enabled ? 8 + j * 18 : 114514; int y = enabled ? 18 + i * 18 : 114514;
                addSlot(new Slot(inventory, index, x, y));
            }
        }
        addSlot(new Slot(inventory, 36, 8 + 4 * 18, 18 * armor) {
            public Identifier getBackgroundSprite() {return EMPTY_HELMET_SLOT_TEXTURE;}
        });
        addSlot(new Slot(inventory, 37, 8 + 5 * 18, 18 * armor) {
            public Identifier getBackgroundSprite() {return EMPTY_CHESTPLATE_SLOT_TEXTURE;}
        });
        addSlot(new Slot(inventory, 38, 8 + 6 * 18, 18 * armor) {
            public Identifier getBackgroundSprite() {return EMPTY_LEGGINGS_SLOT_TEXTURE;}
        });
        addSlot(new Slot(inventory, 39, 8 + 7 * 18, 18 * armor) {
            public Identifier getBackgroundSprite() {return EMPTY_BOOTS_SLOT_TEXTURE;}
        });
        addSlot(new Slot(inventory, 40, 8 + 8 * 18, 18 * armor) {
            public Identifier getBackgroundSprite() {return EMPTY_OFF_HAND_SLOT_TEXTURE;}
        });
        for (i = 0; i < 5; i++) { //饰品栏
            for (j = 0; j < 9; j++) {
                int index = 41 + j + i * 9; boolean enabled = slotsEnabled.contains(index);
                int x = enabled ? 8 + j * 18 : 114514; int y = enabled ? (armor + 1 + i) * 18 : 114514;
                addSlot(new Slot(inventory, index, x, y));
            }
        }
        if (notSelf) addPlayerInventorySlots(inv, rows);
    }

    private static Set<Integer> setup(Inventory inventory, LivingEntity target, boolean editable) {
        Set<Integer> set = new HashSet<>();
        //物品栏
        if (editable) {
            if (target instanceof PlayerEntity player) {
                var inv = player.getInventory().main;
                for (int i = 0; i < inv.size(); i++) {
                    if (i > 35) break;  // 只取前36个槽位
                    inventory.setStack(i, inv.get(i));
                    set.add(i);
                }
            } else if (target instanceof InventoryOwner owner) {
                var stacks = owner.getInventory().heldStacks;
                for (int i = 0; i < stacks.size(); i++) {
                    inventory.setStack(i + 1, stacks.get(i));
                    set.add(i + 1);
                }
            }
        }

        int armorIndex = 0;
        for (ItemStack stack : target.getArmorItems()) {
            inventory.setStack(39 - armorIndex, stack); set.add(39 - armorIndex); armorIndex++;
        } //盔甲栏

        if (!(target instanceof PlayerEntity)) {inventory.setStack(0, target.getMainHandStack()); set.add(0);}
        inventory.setStack(40, target.getOffHandStack()); set.add(40);

        var trinkets = TrinketsApi.getTrinketComponent(target).map(c -> c.getEquipped(s -> true).stream().map(Pair::getRight).toList()).orElse(List.of());
        for (int i = 0; i < trinkets.size(); i++) {
            if (i >= 45) break;
            inventory.setStack(41 + i, trinkets.get(i));
            set.add(41 + i);
        }
        return set;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (editable) {
            if (button == 114 && !player.getWorld().isClient) {
                getSlot(slotIndex).setStack(ItemStack.EMPTY);
                saveInv(inventory, target);
                return;
            }
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
        for (int i = 0; i < 86; i++) {
            ItemStack stack = from.getStack(i);
            if (i == 0 && !(to instanceof PlayerEntity)) to.setStackInHand(Hand.MAIN_HAND, stack);
            if (to instanceof InventoryOwner owner) {
                int size = owner.getInventory().size();
                if (i > 0 && i - 1 < size) owner.getInventory().setStack(i  - 1, stack);
            }
            if (i < 36 && to instanceof PlayerEntity player)  player.getInventory().setStack(i, stack);
            if (i == 36) to.equipStack(EquipmentSlot.HEAD, stack);
            if (i == 37) to.equipStack(EquipmentSlot.CHEST, stack);
            if (i == 38) to.equipStack(EquipmentSlot.LEGS, stack);
            if (i == 39) to.equipStack(EquipmentSlot.FEET, stack);
            if (i == 40) to.setStackInHand(Hand.OFF_HAND, stack);
            if (i >= 41) {
                var pair = findSlot(to, i - 41);
                if (pair != null) pair.getLeft().setStack(pair.getRight(), stack);
            }
        }
    }

    public static Pair<TrinketInventory, Integer> findSlot(LivingEntity entity, int index) {
        //将饰品栏的每一格添加到一个List中，若index与List中的饰品格的序列号相同，则输出该饰品格
        List<Pair<TrinketInventory, Integer>> pairs = new ArrayList<>();
        TrinketsApi.getTrinketComponent(entity).ifPresent(component -> component.getInventory().values().forEach(group -> group.values().forEach(inv -> {
            for (int i = 0; i < inv.size(); i++) pairs.add(new Pair<>(inv, i));
        })));
        for (var pair : pairs) {if (pairs.indexOf(pair) == index) return pair;}
        return null;
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
}
