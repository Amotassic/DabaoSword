package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.SimplePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PileScreenHandler extends AbstractContainerMenu {
    private final CardPileInventory inventory;

    public PileScreenHandler(int syncId, Inventory inv, SimplePayload data) {this(syncId, inv);}

    public PileScreenHandler(int syncId, Inventory inv) {
        super(ModItems.PILE_SCREEN_HANDLER, syncId);
        this.inventory = new CardPileInventory(inv.player);
        if (inv.player instanceof ServerPlayer sp) PVPGameEvents.PLAYER_CARD_PACKS.put(sp, inventory);
        inventory.startOpen(inv.player);
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
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot < 36 ? !this.moveItemStackTo(itemStack2, 4 * 9, this.slots.size(), true) : !this.moveItemStackTo(itemStack2, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public void clicked(int slotIndex, int button, @NonNull ContainerInput actionType, @NonNull Player player) {
        if (button == 114 && !player.level().isClientSide()) {
            ItemStack pile = trinketItem(ModItems.CARD_PILE, player);
            var nbt = getOrCreateNbt(pile);
            int dropped = nbt.getInt("DroppedCards").orElse(0);
            ItemStack stack = getSlot(slotIndex).getItem();
            if (isCard(stack)) { //按下delete键后丢弃卡片，当丢弃3张卡片后，摸一张牌
                cardDiscard(player, d().cards(stack, 1));
                if (dropped == 2) {
                    nbt.remove("DroppedCards");
                    draw(player);
                } else nbt.putInt("DroppedCards", dropped + 1);
                setNbt(pile, nbt);
            }
            return;
        }
        super.clicked(slotIndex, button, actionType, player);
    }

    private static class PileSlot extends Slot {
        public PileSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {return isCard(stack);}
    }
}
