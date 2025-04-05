package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static com.amotassic.dabaosword.item.tool.ArrowRainItem.arrowAround;
import static com.amotassic.dabaosword.item.tool.ArrowRainItem.tridentStorm;

public class Cooldown2Effect extends StatusEffect {
    public Cooldown2Effect() {super(StatusEffectCategory.NEUTRAL,0xFFFFFF);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.COOLDOWN2)).getDuration();

            if (amplifier == 3 && restTime % 2 == 0) { //雷击的效果
                EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()),null);
            }

            if (amplifier == 1 && restTime % 3 == 0) {
                arrowAround(entity, 3, 18, 10, 3);
                arrowAround(entity, 3, 18, 8, 4);
                arrowAround(entity, 3, 18, 6, 5);
                arrowAround(entity, 3, 18, 4, 6);
                arrowAround(entity, 3, 18, 2, 7);
            }

            if (amplifier == 5 && restTime % 4 == 0) {
                tridentStorm(entity, 3, 18, 10, 3);
                tridentStorm(entity, 3, 18, 8, 4);
                tridentStorm(entity, 3, 18, 6, 5);
                tridentStorm(entity, 3, 18, 4, 6);
                tridentStorm(entity, 3, 18, 2, 7);
            }
        }
        return true;
    }
}
