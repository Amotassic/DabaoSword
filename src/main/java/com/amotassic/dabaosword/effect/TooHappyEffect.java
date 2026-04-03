package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class TooHappyEffect extends MobEffect {
    public TooHappyEffect() {super(MobEffectCategory.HARMFUL, 0xF73C0A);}

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {return true;}
    //实现乐不思蜀让生物无法移动
    @Override
    public boolean applyEffectTick(@NonNull ServerLevel serverLevel, LivingEntity entity, int amplification) {
        int restTime = Objects.requireNonNull(entity.getEffect(ModItems.TOO_HAPPY)).getDuration();
        if (restTime <= 1) entity.setPose(Pose.STANDING);
        else {
            if (!(entity instanceof Player)) {entity.setPose(Pose.SLEEPING);}
            entity.setDeltaMovement(0, 0, 0);
        }
        return true;
    }
}
