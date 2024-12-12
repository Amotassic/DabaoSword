package com.amotassic.dabaosword.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface ISha {
    /**原本的伤害处理被取消，改为由杀造成伤害，因此一定要用{@link LivingEntity#damage(DamageSource, float)}来造成伤害
     * @param amount 原本的伤害值*/
    boolean sha(LivingEntity user, LivingEntity target, float amount);

    /**杀已经造成的伤害后，执行后续效果。也就是说，杀如果被闪掉了就不会生效*/
    void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha);
}
