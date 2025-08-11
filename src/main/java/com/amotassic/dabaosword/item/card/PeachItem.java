package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PeachItem extends CardItem.Basic {
    public PeachItem(Settings settings) {super(settings);}

    //非潜行时右键，给自己回血
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && player.getHealth() < player.getMaxHealth() && !player.isSneaking()) {
            onUse(player, player.getStackInHand(hand), hand, player);
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, player, hand);
    }
    //潜行时对生物右键，给其他生物回血
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && entity.getHealth() < entity.getMaxHealth() && user.isSneaking()) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.heal(5);
    }
}
