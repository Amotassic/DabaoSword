package com.amotassic.dabaosword.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public interface PlayerDeathCallback {
    Event<PlayerDeathCallback> EVENT = EventFactory.createArrayBacked(PlayerDeathCallback.class,
        (listeners) -> (player, damageSource) -> {
            for (PlayerDeathCallback event : listeners) {
                event.onDeath(player, damageSource);
            }
        });

    void onDeath(ServerPlayer playerEntity, DamageSource damageSource);
}
