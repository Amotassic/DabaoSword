package com.amotassic.dabaosword.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class RattanArmor extends ArmorItem {

    public RattanArmor(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.rattanarmor.tooltip"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(entity.isSneaking()) return;

        BlockPos entityPos = entity.getBlockPos();
        PlayerEntity player = (PlayerEntity) entity;

        boolean water = entity.getWorld().getFluidState(new BlockPos(entityPos.getX(),
                        (int) (entity.getBoundingBox().getMin(Direction.Axis.Y)), entityPos.getZ()))
                .isOf(Fluids.WATER);
        boolean rattanarmor = player.getInventory().getArmorStack(2).getItem() == ModItems.RATTAN_CHESTPLATE || player.getInventory().getArmorStack(1).getItem() == ModItems.RATTAN_LEGGINGS;

        if(water) {
            if (rattanarmor) {
                Vec3d motion = entity.getVelocity();
                entity.setVelocity(motion.x, 0.0D, motion.z);
                entity.fallDistance = 0;
                entity.setOnGround(true);
            }
        }
    }
}
