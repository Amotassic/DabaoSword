package com.amotassic.dabaosword.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerRespawnCallback {
    Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class,
        (listeners) -> (oldPlayer, newPlayer) -> {
            for (PlayerRespawnCallback event : listeners) {
                event.onPlayerRespawn(oldPlayer, newPlayer);
            }
        });

    void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer);
}
