package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

public class SimpleMenuHandler extends ScreenHandler {
    private final ItemStack stack;
    private final Inventory inventory;
    private final PlayerEntity target;

    public SimpleMenuHandler(int syncId, Inventory inventory, PlayerEntity target) {
        super(ModItems.SIMPLE_MENU_HANDLER, syncId);
        this.inventory = inventory;
        this.stack = inventory.getStack(18);
        this.target = target;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9, 8 + j * 18, 16 + i * 18));
            }
        }
        addSlot(new Slot(inventory, 18, 114514, 114514));
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        ItemStack itemStack = inventory.getStack(slotIndex);
        if (stack.getItem() instanceof Skill skill) skill.onClickGUISlot(player, stack, target, itemStack, slotIndex);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        return !player.hasStatusEffect(ModItems.COOLDOWN2) || (player.hasStatusEffect(ModItems.COOLDOWN2) && Objects.requireNonNull(player.getStatusEffect(ModItems.COOLDOWN2)).getAmplifier() != 2);
    }
}
