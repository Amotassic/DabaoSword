package com.amotassic.dabaosword.effect;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class DefensedEffect extends StatusEffect {
    public DefensedEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("6656ba40-7a9c-a584-3c63-1e1e0e655446"), "Attack Range Lower", -1 - amplifier, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        if (entity instanceof PlayerEntity player) {
            player.getAttributes().addTemporaryModifiers(rangeModifier.get());
        }
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("6656ba40-7a9c-a584-3c63-1e1e0e655446"), "Attack Range Lower", -1 - amplifier, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        if (entity instanceof PlayerEntity player) {
            player.getAttributes().removeModifiers(rangeModifier.get());
        }
        super.onRemoved(entity, attributes, amplifier);
    }
}
