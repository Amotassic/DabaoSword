package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.items.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class InvulnerableEffect extends StatusEffect {
    public InvulnerableEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    //实现决斗的部分效果
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(ModItems.JUEDOUING)) {
            entity.setVelocity(0, 0, 0);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1,255,false,false,false));
        }
    }
    //南蛮入侵召唤物死亡程序
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity.getType() == EntityType.WOLF) {entity.setHealth(0);}
        super.onRemoved(entity, attributes, amplifier);
    }
}
