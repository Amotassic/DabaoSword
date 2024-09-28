package com.amotassic.dabaosword.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public interface Skill {

    //在攻击目标后，造成伤害前触发
    default void preAttack(ItemStack stack, LivingEntity target, PlayerEntity attacker) {}

    //在近战攻击造成伤害后触发
    default void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    //只要攻击造成伤害即可触发，包括远程
    default void postDamage(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    //受到伤害后触发
    default void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {}

    /**
     *当发动技能键按下后，若玩家没有铁骑效果即可触发，需要继承{@link com.amotassic.dabaosword.item.skillcard.SkillItem.ActiveSkill}或者{@link com.amotassic.dabaosword.item.skillcard.SkillItem.ActiveSkillWithTarget}才会生效
     */
    default void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {}

    /**
     * 当实体受到伤害进行伤害值结算时，于原版的盔甲减伤计算前修改伤害值
     * @param target 即将受到伤害的实体
     * <p>
     * 此处为了代码简洁，没有像上面造成伤害后使用的方法一样拆分结算，因此需要自行判断饰品是穿戴于谁身上（target，source.getEntity() 或者source.getAttacker()）
     * @return pair的左边填增伤倍率，右边填固定数值的加减伤害量。增伤倍率可以为负值，结算方式如下：
     * <p>
     * 伤害结算公式：最终伤害 = [原始伤害 x (1 + 增伤倍率和) + 固定数值加减伤] x (1 + 负增伤倍率)。
     * （由此可知：你不可能在完全相同的条件下返回既增伤，又最终减伤，这是我想到最简洁的仅利用两个值就能完成伤害结算的办法）
     */
    default Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
        return new Pair<>(0f, 0f);
    }
}
