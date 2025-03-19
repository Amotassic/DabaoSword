package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.amotassic.dabaosword.api.event.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PileScreenHandler extends ScreenHandler {
    private final CardPileInventory inventory;

    public PileScreenHandler(int syncId, PlayerInventory inv, ActiveSkillPayload data) {this(syncId, inv);}

    public PileScreenHandler(int syncId, PlayerInventory inv) {
        super(ModItems.PILE_SCREEN_HANDLER, syncId);
        this.inventory = new CardPileInventory(inv.player);
        if (inv.player instanceof ServerPlayerEntity sp) PVPGameEvents.PLAYER_CARD_PACKS.put(sp, inventory);
        inventory.onOpen(inv.player);
        int j, k;
        for (j = 0; j < 4; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new PileSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(inv, j, 8 + j * 18, 161));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < 36 ? !this.insertItem(itemStack2, 4 * 9, this.slots.size(), true) : !this.insertItem(itemStack2, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (button == 114 && !player.getWorld().isClient) {
            ItemStack pile = trinketItem(ModItems.CARD_PILE, player);
            NbtCompound nbt = getOrCreateNbt(pile);
            int dropped = nbt.getInt("DroppedCards");
            ItemStack stack = getSlot(slotIndex).getStack();
            if (isCard(stack)) { //按下delete键后丢弃卡片，当丢弃3张卡片后，摸一张牌
                cardDiscard(player, stack, 1, false);
                if (dropped == 2) {
                    nbt.remove("DroppedCards");
                    draw(player);
                } else nbt.putInt("DroppedCards", dropped + 1);
                setNbt(pile, nbt);
            }
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private static class PileSlot extends Slot {
        public PileSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {return isCard(stack);}
    }
}
