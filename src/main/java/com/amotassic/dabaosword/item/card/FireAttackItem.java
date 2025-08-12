package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireAttackItem extends CardItem.Armoury {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            onUse(user, user.getStackInHand(hand), hand, user);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        World world = user.getWorld();
        Vec3d momentum = user.getRotationVector().multiply(3);
        FireballEntity fireballEntity = new FireballEntity(world, user, momentum.getX(), momentum.getY(), momentum.getZ(), 2);
        fireballEntity.addCommandTag("a");
        fireballEntity.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());
        world.spawnEntity(fireballEntity);
    }
}
