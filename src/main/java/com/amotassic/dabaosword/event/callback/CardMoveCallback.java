package com.amotassic.dabaosword.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface CardMoveCallback {
    Event<CardMoveCallback> EVENT = EventFactory.createArrayBacked(CardMoveCallback.class,
            listeners -> (from, to, stack, count, type) -> {
                for (CardMoveCallback listener: listeners){
                    listener.cardMove(from, to, stack, count, type);
                }
            });

    /**
     * Called after an entity's card(s) was moved to other player's inventory
     *
     * @param from The entity who lose the card
     * @param to The player who get the card
     * @param stack The moved stack with count (the next param)
     */
    void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, Type type);

    enum Type {
        INV_TO_INV,
        INV_TO_EQUIP,
        EQUIP_TO_INV,
        EQUIP_TO_EQUIP
    }
}
