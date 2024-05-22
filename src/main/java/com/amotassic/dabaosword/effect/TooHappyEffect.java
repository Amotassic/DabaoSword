package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class TooHappyEffect extends StatusEffect {
    public TooHappyEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
    //实现乐不思蜀让生物无法移动
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.TOO_HAPPY)).getDuration();
        if(restTime<=1) {entity.setPose(EntityPose.STANDING);}
        else {
            if (!(entity instanceof PlayerEntity)) {entity.setPose(EntityPose.SLEEPING);}
            entity.setVelocity(0, 0, 0);
        }
        return true;
    }
}
