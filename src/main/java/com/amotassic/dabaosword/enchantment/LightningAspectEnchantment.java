package com.amotassic.dabaosword.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LightningAspectEnchantment extends Enchantment {

    public LightningAspectEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.FIRE_ASPECT;
    }
    //攻击命中生物后召唤一道闪电的附魔，由于是近战，所以容易伤到自己
    @Override
    public void onTargetDamaged(LivingEntity user,Entity target,int level) {
        if (target instanceof LivingEntity) {
            if (!user.getWorld().isClient()){
                ServerWorld world = (ServerWorld) user.getWorld();
                EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) target.getX(), (int) target.getY(), (int) target.getZ()),null);
            }
        }
    }
}
