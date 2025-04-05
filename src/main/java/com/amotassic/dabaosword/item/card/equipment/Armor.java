package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModifyDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.AQUA;

public class Armor extends Equipment {

    public static class Bagua extends Armor {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int bagua(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            DamageSource source = data.source;
            if (source.getAttacker() instanceof LivingEntity && !user.hasStatusEffect(ModItems.COOLDOWN2)) {
                if (new Random().nextFloat() < 0.5 && !source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
                    voice(user, this);
                    ItemStack shan = new ItemStack(ModItems.SHAN);
                    onUse(user, shan, true, false);
                    ModifyDamage.shan(user, true, source, data.amount);
                    return 1;
                }
            }
            return 0;
        }
    }

    public static class Baiyin extends Armor {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.SELF)
        public int jianshang(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var muls = data.muls;
            if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.getAttacker() instanceof LivingEntity) {
                voice(target, this);
                muls.add(-0.4f);
            }
            return 0;
        }
    }

    public static class Renwang extends Armor {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
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
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip());}

        //实现渡江不沉的效果，代码来自https://github.com/focamacho/RingsOfAscension/中的水上行走戒指
        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.isSneaking()) return;
            World world = entity.getWorld();
            BlockPos pos = entity.getBlockPos();
            boolean water = !world.getFluidState(pos).isOf(Fluids.WATER) && world.getFluidState(pos.down()).isOf(Fluids.WATER);

            if (water && entity.getPos().y - pos.getY() < 0.15) {
                Vec3d motion = entity.getVelocity();
                entity.setVelocity(motion.x, 0.0D, motion.z);
                entity.fallDistance = 0;
                entity.setOnGround(true);
            }
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.SELF)
        public int shouyi(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var adds = data.adds; var amount = data.amount;
            //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！（伤害大于5就只加5）
            if (source.isIn(DamageTypeTags.IS_FIRE)) {
                voice(target, "rattan_armor2");
                adds.add(Math.min(amount, 5f));
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.DROP_TARGET, relation = Relation.ANY)
        public int goodEffect(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getFirst().toStack();
            boolean bl = card.isOf(ModItems.WANJIAN) || card.isOf(ModItems.NANMAN) || card.isOf(ModItems.SHA);
            if (data.targets.contains(user) && bl) {
                voice(user, this); data.removeTarget(user);
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int tengjia(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source;
            //弹射物对藤甲无效
            if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                Entity projectile = source.getSource();
                if (projectile instanceof ArrowEntity) { //即使处于CD中，箭也对藤甲无效
                    projectile.discard(); voice(target, this);
                    return 1;
                }
                if (skill.getCD() == 0) {
                    if (projectile != null) projectile.discard();
                    target.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    skill.setCD(5); voice(target, this);
                    return 1;
                }
            }
            //若攻击者主手没有物品，则无法击穿藤甲
            if (source.getSource() instanceof LivingEntity s && s.getMainHandStack().isEmpty()) {
                if (skill.getCD() == 0) {
                    target.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    skill.setCD(5); voice(target, this);
                    return 1;
                }
            }
            return 0;
        }
    }
}
