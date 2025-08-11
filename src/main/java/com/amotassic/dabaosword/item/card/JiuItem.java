package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class JiuItem extends CardItem.Basic {
    public JiuItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.hasStatusEffect(StatusEffects.STRENGTH) && !world.isClient) {
            onUse(user, user.getStackInHand(hand), hand, user);
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 10, 0));
    }
}
