package com.amotassic.dabaosword.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EndEntityTick {
    public static Event<EndLivingTick> LIVING_EVENT = EventFactory.createArrayBacked(EndLivingTick.class,
        (listeners) -> (living) -> {
            for (EndLivingTick event : listeners) {
                event.endLivingTick(living);
            }
        });

    public static Event<EndPlayerTick> PLAYER_EVENT = EventFactory.createArrayBacked(EndPlayerTick.class,
            (listeners) -> (player) -> {
                for (EndPlayerTick event : listeners) {
                    event.endPlayerTick(player);
                }
            });

    public interface EndLivingTick {
        void endLivingTick(LivingEntity entity);
    }

    public interface EndPlayerTick {
        void endPlayerTick(PlayerEntity player);
    }
}
