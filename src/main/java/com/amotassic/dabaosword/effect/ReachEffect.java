package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
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

import java.util.Objects;
import java.util.UUID;

public class ReachEffect extends StatusEffect {
    public ReachEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier", amplifier + 1, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.REACH, AttributeModifier, ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        if (entity instanceof PlayerEntity player) {
            player.getAttributes().addTemporaryModifiers(rangeModifier.get());
        }
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("7f7dbdb2-0d0d-458a-aa40-ac7633691f66"), "Range modifier", amplifier + 1, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.REACH, AttributeModifier, ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        if (entity instanceof PlayerEntity player) {
            int restTime = Objects.requireNonNull(player.getStatusEffect(ModItems.REACH)).getDuration();
            if(restTime<=1) {
                player.getAttributes().removeModifiers(rangeModifier.get());
            }
        }
    }
}
