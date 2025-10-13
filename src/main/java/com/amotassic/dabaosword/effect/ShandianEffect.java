package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;
import java.util.Random;

public class ShandianEffect extends StatusEffect {
    public ShandianEffect() {super(StatusEffectCategory.HARMFUL, 0x000000);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.SHANDIAN)).getDuration();
        if (restTime % 100 == 0 || restTime <= 1) {
            if (new Random().nextDouble() < 8.0 / 52.0) summonLightning(entity, false, true);
        }
        return true;
    }

    public static void summonLightning(LivingEntity entity, boolean cosmetic, boolean tag) {
        if (entity.getEntityWorld() instanceof ServerWorld world) {
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.MOB_SUMMONED);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                if (cosmetic) lightning.setCosmetic(true);
                if (tag) lightning.addCommandTag("a");
            }
            world.spawnEntity(lightning);
        }
    }
}
