package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.text.Text;

import java.util.Objects;

public class TurnOverEffect extends StatusEffect {
    public TurnOverEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {//给有该效果的生物添加一个名字，便于客户端识别
            entity.setCustomName(Text.literal("翻面"));
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.TURNOVER)).getDuration();
            if (restTime <= 1) entity.setCustomName(null);
        }
        return true;
    }
}
