package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.api.TriPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.lang.invoke.MethodHandle;

public record SkillExecutor(Trigger[] trigger, TriPredicate<LivingEntity, LivingEntity, DamageSource> relation, MethodHandle method) {
    /**技能效果执行
     * @author Amotassic
     * @param user 技能使用者，或者说使技能物品的拥有者
     * @param target 技能事件的触发者
     * @param skill 技能物品
     * @param data 处理技能事件可能用到的额外数据
     */
    public int apply(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
        try {
            return (Integer) method.invokeWithArguments(user, target, skill, data);
        } catch (Throwable e) {
            return 0;
        }
    }
}
