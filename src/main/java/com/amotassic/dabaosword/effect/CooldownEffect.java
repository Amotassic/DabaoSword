package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class CooldownEffect extends StatusEffect {
    public CooldownEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.COOLDOWN)).getDuration();
            if(restTime<=1) {
                player.sendMessage(Text.translatable("dabaosword.cooldown_end").formatted(Formatting.GREEN),true);
            }
        }
        return true;
    }
}
