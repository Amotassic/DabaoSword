package com.amotassic.dabaosword.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShanItem extends CardItem{
    public ShanItem(Settings settings) {
        super(settings);
    }
    //使用后，向前冲刺一段距离，无敌0.5秒，冷却时间0.5秒
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        StatusEffectInstance effect = user.getStatusEffect(ModItems.COOLDOWN);
        if (!(effect != null && effect.getAmplifier() == 1)) {
            Vec3d lookVec = user.getRotationVector();
            Vec3d momentum = lookVec.multiply(3);
            user.setVelocity(momentum.getX(),0 ,momentum.getZ());
            user.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return super.use(world, user, hand);
    }
}
