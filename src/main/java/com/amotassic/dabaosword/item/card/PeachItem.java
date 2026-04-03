package com.amotassic.dabaosword.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class PeachItem extends CardItem.Basic {
    public PeachItem(Properties settings) {super(settings);}

    //非潜行时右键，给自己回血
    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        if (!world.isClientSide() && player.getHealth() < player.getMaxHealth() && !player.isShiftKeyDown()) {
            onUse(player, player.getItemInHand(hand), hand, player);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, player, hand);
    }
    //潜行时对生物右键，给其他生物回血
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && entity.getHealth() < entity.getMaxHealth() && user.isShiftKeyDown()) {
            onUse(user, user.getItemInHand(hand), hand, entity);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.heal(5);
    }
}
