package com.amotassic.dabaosword.enchantment;

import com.amotassic.dabaosword.Sounds;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

import java.util.Random;

public class RageNatureEnchantment extends Enchantment {
    public RageNatureEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != ModItems.POJUN;
    }

    public boolean isTreasure() {
        return true;
    }
    //攻击命中敌人，回复5点血或者摸一张牌
    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (user instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN)) {
            if (new Random().nextFloat() < 0.5) {player.heal(5);}
            else {player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));}
            if (new Random().nextFloat() < 0.5) {
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.KUANGGU1, SoundCategory.PLAYERS, 2.0F, 1.0F);
            } else {
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.KUANGGU2, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 10,0,false,true,true));
        }
    }
}
