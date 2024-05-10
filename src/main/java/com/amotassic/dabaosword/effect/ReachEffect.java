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
        if (entity instanceof PlayerEntity player) gainReach(player,amplifier + 1);
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            int restTime = Objects.requireNonNull(player.getStatusEffect(ModItems.REACH)).getDuration();
            if(restTime<=1) gainReach(player, 0);
        }
    }

    private void gainReach(LivingEntity entity, int value) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("2b3df518-6e44-3554-821b-232333bcef5b"), "Range modifier", value, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.REACH, AttributeModifier, ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        entity.getAttributes().addTemporaryModifiers(rangeModifier.get());
    }
}
