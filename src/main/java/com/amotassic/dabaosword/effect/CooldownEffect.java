package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class CooldownEffect extends MobEffect {
    public CooldownEffect() {super(MobEffectCategory.NEUTRAL,0xFFFFFF);}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, @NonNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            int restTime = Objects.requireNonNull(entity.getEffect(ModItems.COOLDOWN)).getDuration();
            if(restTime<=1) {
                player.sendOverlayMessage(Component.translatable("dabaosword.cooldown_end").withStyle(ChatFormatting.GREEN));
            }
        }
        return true;
    }
}
