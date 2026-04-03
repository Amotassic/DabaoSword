package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static com.amotassic.dabaosword.item.tool.ArrowRainItem.arrowAround;
import static com.amotassic.dabaosword.item.tool.ArrowRainItem.tridentStorm;

public class Cooldown2Effect extends MobEffect {
    public Cooldown2Effect() {super(MobEffectCategory.NEUTRAL,0xFFFFFF);}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, LivingEntity entity, int amplifier) {
        int restTime = Objects.requireNonNull(entity.getEffect(ModItems.COOLDOWN2)).getDuration();

        if (amplifier == 3 && restTime % 2 == 0) { //雷击的效果
            EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()), EntitySpawnReason.MOB_SUMMONED);
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
        return true;
    }
}
