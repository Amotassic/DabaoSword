package com.amotassic.dabaosword.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface CardDiscardCallback {
    Event<CardDiscardCallback> EVENT = EventFactory.createArrayBacked(CardDiscardCallback.class,
            listeners -> (player, stack, count, fromEquip) -> {
                for (CardDiscardCallback listener: listeners){
                    listener.cardDiscard(player, stack, count, fromEquip);
                }
            });

    /**
     * Called after a player's card(s) was discarded
     *
     * @param player The player who discards the card
     * @param stack The discarded stack
     */
    void cardDiscard(PlayerEntity player, ItemStack stack, int count, boolean fromEquip);
}
