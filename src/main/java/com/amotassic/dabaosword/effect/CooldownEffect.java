package com.amotassic.dabaosword.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CooldownEffect extends StatusEffect {
    public CooldownEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        PlayerEntity player = (PlayerEntity) entity;
        ServerWorld serverWorld = (ServerWorld) entity.getWorld();
        if (amplifier !=1) {
            player.sendMessage(Text.translatable("dabaosword.cooldown_end").formatted(Formatting.GREEN),true);
        }
    }
}
