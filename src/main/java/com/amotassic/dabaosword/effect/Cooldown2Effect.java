package com.amotassic.dabaosword.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import static com.amotassic.dabaosword.item.card.ArrowRainItem.arrowRain;

public class Cooldown2Effect extends StatusEffect {
    public Cooldown2Effect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    //一级效果被用于万箭齐发
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (amplifier == 1 && entity instanceof PlayerEntity player && !player.getWorld().isClient) {
            arrowRain(player);
        }
        super.applyUpdateEffect(entity, amplifier);
    }
}
