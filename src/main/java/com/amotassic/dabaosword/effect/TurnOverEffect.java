package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class TurnOverEffect extends MobEffect {
    public TurnOverEffect() {super(MobEffectCategory.HARMFUL, 0x07050F);}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {//给有该效果的生物添加一个名字，便于客户端识别
            entity.setCustomName(Component.literal("翻面"));
            int restTime = Objects.requireNonNull(entity.getEffect(ModItems.TURNOVER)).getDuration();
            if (restTime <= 1) entity.setCustomName(null);
        }
        return true;
    }
}
