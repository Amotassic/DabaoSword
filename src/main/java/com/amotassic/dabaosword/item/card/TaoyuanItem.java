package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class TaoyuanItem extends CardItem.Armoury {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld sw) {
            Set<LivingEntity> targets = new HashSet<>(sw.getPlayers());
            onUse(user, user.getStackInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.heal(5.0F);
        if (target != user) voice(target, this);
    }
}
