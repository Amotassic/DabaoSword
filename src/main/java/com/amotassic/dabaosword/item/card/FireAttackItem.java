package com.amotassic.dabaosword.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class FireAttackItem extends CardItem.Armoury {
    public FireAttackItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide()) {
            onUse(user, user.getItemInHand(hand), hand, user);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        var world = user.level();
        Vec3 momentum = user.getForward().scale(3);
        LargeFireball fireballEntity = new LargeFireball(world, user, momentum, 2);
        fireballEntity.addTag("a");
        fireballEntity.setPos(user.getX(), user.getY(0.5) + 0.5, user.getZ());
        world.addFreshEntity(fireballEntity);
    }
}
