package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Objects;

public class TurnOverEffect extends StatusEffect {
    public TurnOverEffect() {super(StatusEffectCategory.HARMFUL, 0x07050F);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {//给有该效果的生物添加一个名字，便于客户端识别
            entity.setCustomName(Text.literal("翻面"));
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.TURNOVER)).getDuration();
            if (restTime <= 1) entity.setCustomName(null);
        }
        return true;
    }
}
