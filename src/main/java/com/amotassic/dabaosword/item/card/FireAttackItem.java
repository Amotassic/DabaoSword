package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireAttackItem extends CardItem implements ModTools {
    public FireAttackItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            Vec3d momentum = user.getRotationVector().multiply(3);
            FireballEntity fireballEntity = new FireballEntity(world, user, momentum.getX(), momentum.getY() ,momentum.getZ(), 3);
            fireballEntity.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());
            world.spawnEntity(fireballEntity);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
            voice(user, Sounds.HUOGONG);
        }
        return super.use(world, user, hand);
    }
}
