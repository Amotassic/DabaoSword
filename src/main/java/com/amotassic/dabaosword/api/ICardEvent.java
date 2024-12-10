package com.amotassic.dabaosword.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface ICardEvent {

    /**如果卡牌所选目标拥有某个技能，卡牌是否能够生效
     * @param skill 目标玩家的技能*/
    default boolean canUseIfTargetHasSkill(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {return true;}

    /**当卡牌使用后，如果使用者有技能，则触发该技能的效果
     * @param skill 使用者能在使用牌之后发动的技能*/
    default void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {}

    /**当即将被弃置牌的生物拥有技能，是否允许卡牌被弃置
     * @param entity 即将执行弃牌效果的生物
     * @param skill 即将弃置牌的生物的技能*/
    default boolean shouldDiscard(LivingEntity entity, ItemStack card, int count, boolean fromEquip, ItemStack skill) {return true;}

    /**当卡牌被弃置后，若弃牌者有技能，则触发该技能的效果，需要注意判断弃牌者是否死亡
     * @param skill 弃牌者要触发的技能*/
    default void onCardDiscard(LivingEntity entity, ItemStack card, int count, boolean fromEquip, ItemStack skill) {}

    /**当卡牌从一个实体身上移动到另一个实体身上时，触发失去卡牌者的技能
     * @param skill 卡牌来源的实体要触发的技能
     * @param fromEquip 卡牌是否来自装备区，false表示来自手牌区
     * @param toEquip 卡牌是否要移动到目标实体的装备区，false表示移动到手牌区*/
    default void onCardMove(LivingEntity from, ItemStack skill, LivingEntity to, ItemStack card, int count, boolean fromEquip, boolean toEquip) {}

    /**即将承受卡牌带来的伤害时，触发即将受伤者的技能*/
    default boolean canHurtByCard(LivingEntity entity, ItemStack skill, ItemStack card, DamageSource source) {return true;}

    /**当受到卡牌带来的伤害时，触发受伤者的技能*/
    default void onHurtByCard(LivingEntity entity, ItemStack skill, ItemStack card) {}
}
