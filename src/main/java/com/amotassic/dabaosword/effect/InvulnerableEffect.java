package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.Objects;

public class InvulnerableEffect extends StatusEffect {
    public InvulnerableEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        //南蛮入侵召唤物死亡程序
        if (entity.getType() == EntityType.WOLF) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.INVULNERABLE)).getDuration();
            if(restTime<=1) {
                entity.setHealth(0);
            }
        }
        return true;
    }
}
