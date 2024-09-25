package com.amotassic.dabaosword.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface Skill {

    //在攻击目标后，造成伤害前触发
    default void preAttack(ItemStack stack, LivingEntity target, PlayerEntity attacker) {}

    //在近战攻击造成伤害后触发
    default void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    //只要攻击造成伤害即可触发，包括远程
    default void postDamage(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    //受到伤害后触发
    default void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {}
}
