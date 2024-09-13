package com.amotassic.dabaosword.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CardUsePostCallback {
    Event<CardUsePostCallback> EVENT = EventFactory.createArrayBacked(CardUsePostCallback.class,
            listeners -> (user, stack, target) -> {
                for (CardUsePostCallback listener: listeners){
                    listener.cardUsePost(user, stack, target);
                }
            });

    /**
     * Called after a player used a card to the target
     *
     * @param user The player who used the card
     * @param stack The used card stack
     * @param target The target entity
     */
    void cardUsePost(PlayerEntity user, ItemStack stack, @Nullable LivingEntity target);
}
