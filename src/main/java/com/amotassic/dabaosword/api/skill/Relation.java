package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.api.TriPredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 全局监听技能触发时，以此判断技能拥有者与技能触发者的关系
 * @author Amotassic
 */
public enum Relation {
    /**任意关系，或者说没有关系*/
    ANY,
    /**技能拥有者与技能触发者为同一实体*/
    SELF,
    /**与上一个相反，技能拥有者与技能触发者不是同一实体*/
    NOT_SELF,
    /**技能拥有者攻击了技能触发者，不论近战还是远程，默认两者不是同一实体*/
    ATTACKER,
    /**技能拥有者攻击了技能触发者，不论近战还是远程，包括两者是同一实体的情况*/
    ATTACKER_SELF,
    /**技能拥有者直接攻击了技能触发者，即仅近战，默认两者不是同一实体*/
    DIRECT_ATTACKER,
    /**技能拥有者直接攻击了技能触发者，即仅近战，包括两者是同一实体的情况*/
    DIRECT_ATTACKER_SELF,
    /**技能拥有者击杀了技能触发者，默认两者不是同一实体*/
    KILLER,
    /**技能拥有者击杀了技能触发者，包括两者是同一实体的情况*/
    KILLER_SELF;

    public static TriPredicate<LivingEntity, LivingEntity, DamageSource> getPredicate(Relation re) {
        return switch (re) {
            case ANY ->                     ISkill.ANY;
            case SELF ->                    ISkill.SELF;
            case NOT_SELF ->                ISkill.NOT_SELF;
            case ATTACKER ->                ISkill.ATTACKER.and(ISkill.NOT_SELF);
            case ATTACKER_SELF ->           ISkill.ATTACKER;
            case DIRECT_ATTACKER ->         ISkill.DIRECT_ATTACKER.and(ISkill.NOT_SELF);
            case DIRECT_ATTACKER_SELF ->    ISkill.DIRECT_ATTACKER;
            case KILLER ->                  ISkill.KILLER.and(ISkill.NOT_SELF);
            case KILLER_SELF ->             ISkill.KILLER;
        };
    }
}
