package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class InvulnerableEffect extends StatusEffect {
    public InvulnerableEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    //移除后加0.5秒冷却时间
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 10,1,false,false,false));
    }
}
