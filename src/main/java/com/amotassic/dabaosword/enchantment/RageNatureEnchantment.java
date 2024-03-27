package com.amotassic.dabaosword.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import java.util.Random;

public class RageNatureEnchantment extends Enchantment {
    public RageNatureEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    public boolean isTreasure() {
        return true;
    }
    //攻击命中敌人，就能回1点血，就一行代码，效果出奇的好
    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (new Random().nextFloat() < 0.5) user.heal(0.5f);
    }
}
