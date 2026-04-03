package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Random;

public class ShandianEffect extends MobEffect {
    public ShandianEffect() {super(MobEffectCategory.HARMFUL, 0x000000);}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}
    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, LivingEntity entity, int amplifier) {
        int restTime = Objects.requireNonNull(entity.getEffect(ModItems.SHANDIAN)).getDuration();
        if (restTime % 100 == 0 || restTime <= 1) {
            if (new Random().nextDouble() < 8.0 / 52.0) summonLightning(entity, false, true);
        }
        return true;
    }

    public static void summonLightning(LivingEntity entity, boolean cosmetic, boolean tag) {
        if (entity.level() instanceof ServerLevel world) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (lightning != null) {
                lightning.teleportTo(entity.getX(), entity.getY(), entity.getZ());
                if (cosmetic) lightning.setVisualOnly(true);
                if (tag) lightning.addTag("a");
                world.addFreshEntity(lightning);
            }
        }
    }
}
