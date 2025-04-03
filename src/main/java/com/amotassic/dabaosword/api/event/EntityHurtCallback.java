package com.amotassic.dabaosword.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface EntityHurtCallback {
    Event<EntityHurtCallback> EVENT = EventFactory.createArrayBacked(EntityHurtCallback.class,
            (listeners) -> (entity, source, amount) -> {
                for (EntityHurtCallback event : listeners) {
                    event.hurtEntity(entity, source, amount);
                }
            });

    void hurtEntity(LivingEntity entity, DamageSource source, float amount);
}
