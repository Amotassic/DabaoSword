package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class FireAttackItem extends CardItem{
    public FireAttackItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.dabaosword.huogong.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Vec3d momentum = user.getRotationVector().multiply(3);
        FireballEntity fireballEntity = new FireballEntity(world, user, momentum.getX(), momentum.getY() ,momentum.getZ(), 3);
        fireballEntity.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());
        world.spawnEntity(fireballEntity);
        if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.HUOGONG, SoundCategory.PLAYERS, 2.0F, 1.0F);
        return super.use(world, user, hand);
    }
}
