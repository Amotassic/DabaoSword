package com.amotassic.dabaosword.item.skillcard;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CanDisable extends SkillItem {
    public CanDisable(Settings settings) {super(settings);}

    //右键使用控制是否启用
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.getDamage() == 0) {stack.setDamage(1);}
            else {stack.setDamage(0);}
        }
        return super.use(world, user, hand);
    }
}
