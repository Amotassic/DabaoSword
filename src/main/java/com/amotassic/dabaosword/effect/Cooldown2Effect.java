package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static com.amotassic.dabaosword.item.equipment.ArrowRainItem.arrowRain;

public class Cooldown2Effect extends StatusEffect {
    public Cooldown2Effect() {super(StatusEffectCategory.NEUTRAL,0xFFFFFF);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.COOLDOWN2)).getDuration();
            //一级效果被用于万箭齐发
            if (amplifier == 1 && restTime % 5 == 0) arrowRain(entity, 3, 25);

            if (amplifier == 3 && restTime % 2 == 0) { //雷击的效果
                EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()),null);
            }
        }
        return true;
    }
}
