package com.amotassic.dabaosword.enchantment;

import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.Random;

public class PojunEnchantment extends Enchantment {
    public PojunEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    public boolean isTreasure() {
        return true;
    }
    //可以附魔在斧头上
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (stack.getItem() instanceof AxeItem) {
            return true;
        }
        return super.isAcceptableItem(stack);
    }
    //攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity entity && user.getWorld() instanceof ServerWorld serverWorld && !user.hasStatusEffect(ModItems.COOLDOWN)) {
            ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
            if (!head.isEmpty()) {entity.dropStack(head.copy());head.setCount(0);}
            if (!chest.isEmpty()) {entity.dropStack(chest.copy());chest.setCount(0);}
            if (!legs.isEmpty()) {entity.dropStack(legs.copy());legs.setCount(0);}
            if (!feet.isEmpty()) {entity.dropStack(feet.copy());feet.setCount(0);}
            if (new Random().nextFloat() < 0.5) {
                serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.POJUN1, SoundCategory.PLAYERS, 2.0F, 1.0F);
            } else {
                serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.POJUN2, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
            user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 50,0,false,true,true));
        }
    }
}
