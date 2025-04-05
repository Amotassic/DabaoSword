package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class JiuItem extends CardItem.Basic {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.hasStatusEffect(StatusEffects.STRENGTH) && !world.isClient && hand == Hand.MAIN_HAND) {
            onUse(user, user.getMainHandStack(), user);
            return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 10, 0));
    }
}
