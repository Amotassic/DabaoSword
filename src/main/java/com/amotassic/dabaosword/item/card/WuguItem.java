package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class WuguItem extends CardItem.Armoury {
    public WuguItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld sw) {
            Set<LivingEntity> targets = new HashSet<>(sw.getPlayers());
            onUse(user, user.getStackInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            player.getHungerManager().add(5, 1.0f);
            if (player != user) voice(player, this);
        }
    }
}
