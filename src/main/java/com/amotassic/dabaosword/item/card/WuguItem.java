package com.amotassic.dabaosword.item.card;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class WuguItem extends CardItem.Armoury {
    public WuguItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (world instanceof ServerLevel sw) {
            Set<LivingEntity> targets = new HashSet<>(sw.players());
            onUse(user, user.getItemInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        if (target instanceof Player player) {
            player.getFoodData().eat(5, 1.0f);
            if (player != user) voice(player, this);
        }
    }
}
