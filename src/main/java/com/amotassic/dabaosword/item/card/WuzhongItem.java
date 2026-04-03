package com.amotassic.dabaosword.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.util.ModTools.draw;

public class WuzhongItem extends CardItem.Armoury {
    public WuzhongItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide()) {
            onUse(user, user.getItemInHand(hand), hand, user);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {draw(target, 2);}
}
