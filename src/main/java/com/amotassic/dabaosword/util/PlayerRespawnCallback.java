package com.amotassic.dabaosword.util;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerRespawnCallback {
    Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class,
        (listeners) -> (oldPlayer, newPlayer) -> {
            for (PlayerRespawnCallback event : listeners) {
                event.onPlayerRespawn(oldPlayer, newPlayer);
            }
        });

    void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer);
}
