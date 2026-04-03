package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModifyDamage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.AQUA;

public class Armor extends Equipment {
    public Armor(Properties settings) {super(settings);}

    public static class Bagua extends Armor {
        public Bagua(Properties settings) {super(settings);}

        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int bagua(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            DamageSource source = data.source;
            if (source.getEntity() instanceof LivingEntity && !user.hasEffect(ModItems.COOLDOWN2)) {
                if (new Random().nextFloat() < 0.5 && !source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                    voice(user, this);
                    ItemStack shan = new ItemStack(ModItems.SHAN);
                    onUse(user, shan, null, true, false);
                    ModifyDamage.shan(user, true, source, data.amount);
                    return 1;
                }
            }
            return 0;
        }
    }

    public static class Baiyin extends Armor {
        public Baiyin(Properties settings) {super(settings);}

        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.SELF)
        public int jianshang(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var muls = data.muls;
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.getEntity() instanceof LivingEntity) {
                voice(target, this);
                muls.add(-0.4f);
            }
            return 0;
        }

        @SkillInfo(trigger = {Trigger.LOSE_CARD_DISCARD, Trigger.LOSE_CARD_MOVE}, relation = Relation.SELF)
        public int recover(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user.isAlive() && user.getHealth() < user.getMaxHealth()) {
                for (var card : data.cards_from_equ.keySet()) {
                    if (card.toStack().is(this)) {
                        voice(user, this);
                        user.heal(5);
                    }
                }
            }
            return 0;
        }
    }

    public static class Renwang extends Armor {
        public Renwang(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.DROP_TARGET, relation = Relation.ANY)
        public int fangheisha(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.targets.contains(user) && isSha.and(isBlackCard).test(data.getFirst().toStack())) {
                voice(user, this); data.removeTarget(user);
            }
            return 0;
        }
    }

    public static class Rattan extends Armor {
        public Rattan(Properties settings) {super(settings);}

        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip());}

        //实现渡江不沉的效果，代码来自https://github.com/focamacho/RingsOfAscension/中的水上行走戒指
        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.isShiftKeyDown()) return;
            var world = entity.level();
            BlockPos pos = entity.getOnPos();
            boolean water = !world.getFluidState(pos).is(Fluids.WATER) && world.getFluidState(pos.below()).is(Fluids.WATER);

            if (water && entity.position().y - pos.getY() < 0.15) {
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x, 0.0D, motion.z);
                entity.fallDistance = 0;
                entity.setOnGround(true);
            }
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.SELF)
        public int shouyi(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var adds = data.adds; var amount = data.amount;
            //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！（伤害大于5就只加5）
            if (source.is(DamageTypeTags.IS_FIRE)) {
                voice(target, "rattan_armor2");
                adds.add(Math.min(amount, 5f));
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.DROP_TARGET, relation = Relation.ANY)
        public int goodEffect(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getFirst().toStack();
            boolean bl = card.is(ModItems.WANJIAN) || card.is(ModItems.NANMAN) || card.is(ModItems.SHA);
            if (data.targets.contains(user) && bl) {
                voice(user, this); data.removeTarget(user);
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int tengjia(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source;
            //弹射物对藤甲无效
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                Entity projectile = source.getDirectEntity();
                if (projectile instanceof Arrow) { //即使处于CD中，箭也对藤甲无效
                    projectile.discard(); voice(target, this);
                    return 1;
                }
                if (skill.getCD() == 0) {
                    if (projectile != null) projectile.discard();
                    target.addEffect(new MobEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    skill.setCD(5); voice(target, this);
                    return 1;
                }
            }
            //若攻击者主手没有物品，则无法击穿藤甲
            if (source.getDirectEntity() instanceof LivingEntity s && s.getMainHandItem().isEmpty()) {
                if (skill.getCD() == 0) {
                    target.addEffect(new MobEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    skill.setCD(5); voice(target, this);
                    return 1;
                }
            }
            return 0;
        }
    }
}
