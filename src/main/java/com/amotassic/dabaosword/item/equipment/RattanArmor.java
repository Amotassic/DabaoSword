package com.amotassic.dabaosword.item.equipment;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RattanArmor extends EquipmentItem {
    public RattanArmor(Settings settings) {super(settings);}

    //实现渡江不沉的效果，代码来自https://github.com/focamacho/RingsOfAscension/中的水上行走戒指

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if(entity.isSneaking()) return;

        BlockPos entityPos = entity.getBlockPos();

        boolean water = entity.getWorld().getFluidState(new BlockPos(entityPos.getX(),
                        (int) (entity.getBoundingBox().getMin(Direction.Axis.Y)), entityPos.getZ()))
                .isOf(Fluids.WATER);

        if(water) {
            Vec3d motion = entity.getVelocity();
            entity.setVelocity(motion.x, 0.0D, motion.z);
            entity.fallDistance = 0;
            entity.setOnGround(true);
        }
    }
}
