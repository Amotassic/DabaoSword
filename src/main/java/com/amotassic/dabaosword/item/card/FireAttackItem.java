package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.*;

public class FireAttackItem extends CardItem {
    public FireAttackItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            Vec3d momentum = user.getRotationVector().multiply(3);
            FireballEntity fireballEntity = new FireballEntity(world, user, momentum.getX(), momentum.getY() ,momentum.getZ(), 3);
            fireballEntity.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());
            world.spawnEntity(fireballEntity);
            voice(user, Sounds.HUOGONG);
            CardUsePostCallback.EVENT.invoker().cardUsePost(user, user.getStackInHand(hand), null);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
