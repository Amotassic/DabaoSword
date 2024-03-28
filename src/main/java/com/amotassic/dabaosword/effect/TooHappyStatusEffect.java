package com.amotassic.dabaosword.effect;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class TooHappyStatusEffect extends StatusEffect {
    public TooHappyStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    //实现乐不思蜀让生物无法移动
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setPose(EntityPose.SLEEPING);
        entity.setVelocity(0, 0, 0);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2,255,false,false,false));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setPose(EntityPose.STANDING);
        entity.removeStatusEffect(StatusEffects.SLOWNESS);
    }
}
