package com.amotassic.dabaosword.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.function.TriFunction;

public interface Skill {
    /**在攻击目标后，造成伤害前触发*/
    default void preAttack(ItemStack stack, LivingEntity target, PlayerEntity attacker) {}

    /**在近战攻击造成伤害后触发*/
    default void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    /**只要攻击造成伤害即可触发，包括远程*/
    default void postDamage(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {}

    /**受到伤害后触发*/
    default void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {}

    /**当发动技能键按下后，若玩家没有铁骑效果即可触发，需要继承{@link com.amotassic.dabaosword.item.skillcard.SkillItem.ActiveSkill}或者{@link com.amotassic.dabaosword.item.skillcard.SkillItem.ActiveSkillWithTarget}才会生效*/
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
    default Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {return null;}

    //========================================分割线========================================//

    /**@see CancelDamageData*/
    default CancelDamageData cancelDamage() {return null;}

    /**取消伤害，在伤害结算之前触发。为了只用一个方法实现，我把取消伤害优先度和技能效果合并到一起了。
     * @param priority 取消伤害的优先度，若为null则不触发取消伤害
     * @param effect 取消伤害时触发的附加效果，若该方法返回true则会取消伤害。其中：
     *               {@link LivingEntity} 为即将受到伤害的实体
     *               {@link DamageSource} 为伤害来源
     *               {@link Float} 为本次受伤的伤害值
     */
    record CancelDamageData(Priority priority, TriFunction<LivingEntity, DamageSource, Float, Boolean> effect) {}
    enum Priority {
        /**最高优先级，高于buff但低于原版的伤害免疫检查。但真的会用到吗？（划掉，已经用于帷幕了）*/
        HIGHEST,
        /**高优先级，一般用于不产生消耗的装备，如藤甲、八卦阵*/
        HIGH,
        /**一般优先级，一般用于技能，如流离*/
        NORMAL,
        /**低优先级，一般用于卡牌，会产生消耗，如闪*/
        LOW,
        /**最低优先级，用于确认已经绕过其余所有免伤造成伤害后，最后取消伤害，如绝情：造成伤害后触发*/
        LOWEST
    }

    /**当玩家发动技能打开GUI界面后，点击GUI界面某个非空槽位时触发*/
    default void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slotIndex) {}

    /**未完成，勿用！！！在打开GUI界面后，玩家是否能手动关闭GUI*/
    default boolean canCloseGUI(ItemStack stack) {return true;}

    /**摸牌阶段开始时触发
     * @return 摸牌阶段多摸牌的数量，返回负值就减少摸牌数，若返回值小于等于-114，直接取消摸牌。*/
    default int onDrawPhase(PlayerEntity player, ItemStack stack) {return 0;}
}
