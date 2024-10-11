package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ModifyDamage {
    //由于fabric没有提供修改伤害的方法，因此我自己注入"applyArmorToDamage"以修改生物受到的伤害值
    public static float modify(LivingEntity entity, DamageSource source, float value) {
        float multiply = 0; //倍率增伤乘区
        float add = 0; //固定数值加减伤害区
        List<Float> reducing = new ArrayList<>(); //最终减伤乘区，存储负值，每个值使最终伤害为原来的（1+x）倍

        //处理所有饰品带来的增/减伤结算
        List<Pair<Pair<Float, Float>, List<Float>>> pairList = new ArrayList<>();
        pairList.add(calculateDMG(entity, source, value, entity));
        if (source.getSource() instanceof LivingEntity SE) {
            if (!SE.getCommandTags().contains("sha")) { //防止杀的效果再次触发近战加伤
                pairList.add(calculateDMG(entity, source, value, SE));

                //插入一个武器版古锭刀的结算
                int i = 0; //i == 4则说明受击者的盔甲栏没有任何物品
                for (var s : entity.getArmorItems()) {if (s.isEmpty()) i++;}
                if (i == 4 && SE.getMainHandStack().isOf(ModItems.GUDINGDAO)) multiply += 1;
            } //这里的这个else很重要！防止两个条件同时满足时会触发双重结算
        } else if (source.getAttacker() instanceof LivingEntity AT) pairList.add(calculateDMG(entity, source, value, AT));
        for (var p : pairList) {
            multiply += p.getLeft().getLeft();
            add += p.getLeft().getRight();
            reducing.addAll(p.getRight());
        }

        //伤害结算
        value = value * (1 + multiply) + add;
        for (var f : reducing) {value *= (1 + f);}

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            entity.damageArmor(source, value);
            value = DamageUtil.getDamageLeft(value, entity.getArmor(), (float)entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return value;
    }

    private static Pair<Pair<Float, Float>, List<Float>> calculateDMG(LivingEntity entity, DamageSource source, float value, LivingEntity trinketOwner) {
        float m = 0; float a = 0;
        List<Float> r = new ArrayList<>();
        for (var p : allTrinkets(trinketOwner)) {
            var stack = p.getRight();
            Pair<Float, Float> fp = new Pair<>(0f, 0f);
            if (stack.getItem() instanceof SkillItem skill) fp = skill.modifyDamage(entity, source, value);
            if (stack.getItem() instanceof Equipment skill) fp = skill.modifyDamage(entity, source, value);

            if (fp.getLeft() < 0) r.add(fp.getLeft()); else m += fp.getLeft();
            a += fp.getRight();
        }
        return new Pair<>(new Pair<>(m, a), r);
    }

    private static List<List<ItemStack>> eventStacks(LivingEntity entity, DamageSource source, float value, LivingEntity trinketOwner) {
        List<List<ItemStack>> stacks = new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        for (var p : allTrinkets(trinketOwner)) {
            var stack = p.getRight();
            Skill.Priority priority = null;

            if (stack.getItem() instanceof SkillItem skill) priority = skill.getPriority(entity, source, value);
            if (stack.getItem() instanceof Equipment skill) priority = skill.getPriority(entity, source, value);

            if (priority != null) stacks.get(priority.ordinal()).add(stack);
        }
        return stacks;
    }

    private static boolean execute(LivingEntity entity, DamageSource source, float amount, List<List<ItemStack>> list, int index) {
        for (var s : list.get(index)) {
            boolean cancel = false;
            if (s.getItem() instanceof Equipment skill) cancel = skill.cancelDamage(entity, source, amount);
            if (s.getItem() instanceof SkillItem skill) cancel = skill.cancelDamage(entity, source, amount);
            if (cancel) return true;
        }
        return false;
    }

    //取消伤害结算：先获取所有输出了结算优先度的ItemStack，将它们按照优先度依次排列（见eventStacks方法），再分别结算（见execute方法）
    public static boolean shouldCancel(LivingEntity entity, DamageSource source, float amount) {
        //检查事件优先度，获取输出了优先度的stack
        List<List<ItemStack>> list = eventStacks(entity, source, amount, entity);
        List<List<ItemStack>> l1 = null; List<List<ItemStack>> l2 = null;
        if (source.getSource() instanceof LivingEntity SE) l1 = eventStacks(entity, source, amount, SE);
        else if (source.getAttacker() instanceof LivingEntity AT) l2 = eventStacks(entity, source, amount, AT);
        //合并3个list中的所有优先度和stack
        for (int i = 0; i < 5; i++) {
            if (l1 != null) list.get(i).addAll(l1.get(i));
            if (l2 != null) list.get(i).addAll(l2.get(i));
        }
        //0.最高优先度执行，暂无用途
        if (execute(entity, source, amount, list, 0)) return true;
        //无敌效果
        if (entity.hasStatusEffect(ModItems.INVULNERABLE) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        if (source.getSource() instanceof LivingEntity SE) {
            //被乐的生物无法造成伤害
            if (SE.hasStatusEffect(ModItems.TOO_HAPPY)) return true;
            //沈佳宜防御效果
            if (!(SE instanceof PlayerEntity) && entity.hasStatusEffect(ModItems.DEFEND)) {
                if (Objects.requireNonNull(entity.getStatusEffect(ModItems.DEFEND)).getAmplifier() >= 2) return true;
            }
            //决斗等物品虽然手长，但过远时普通伤害无效
            if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && entity.distanceTo(SE) > 5) {
                if (SE.getMainHandStack().isOf(ModItems.JUEDOU) || SE.getMainHandStack().isOf(ModItems.DISCARD)) return true;
            }
        } else if (source.getAttacker() instanceof LivingEntity AT) {
            //被乐的生物无法造成伤害
            if (AT.hasStatusEffect(ModItems.TOO_HAPPY)) return true;
        }
        //1.高优先度执行：装备
        if (execute(entity, source, amount, list, 1)) return true;
        //2.一般优先度执行：技能
        if (execute(entity, source, amount, list, 2)) return true;
        //3.低优先度执行：卡牌 闪以及响应南蛮的杀
        if (execute(entity, source, amount, list, 3)) return true;
        if (source.getSource() instanceof WolfEntity dog && dog.hasStatusEffect(ModItems.INVULNERABLE)) {
            //被南蛮入侵的狗打中可以消耗杀以免疫伤害
            if (entity instanceof PlayerEntity player) {
                dog.setHealth(0);
                if (hasCard(player, isSha)) {
                    var stack = getCard(player, isSha).getRight();
                    if (stack.isOf(ModItems.SHA)) voice(player, Sounds.SHA);
                    if (stack.isOf(ModItems.FIRE_SHA)) voice(player, Sounds.SHA_FIRE);
                    if (stack.isOf(ModItems.THUNDER_SHA)) voice(player, Sounds.SHA_THUNDER);
                    nonPreUseCardDecrement(player, stack, null);
                    return true;
                }
            }
        }
        if (source.getAttacker() instanceof LivingEntity) {
            if (!entity.hasStatusEffect(ModItems.COOLDOWN2) && !entity.getCommandTags().contains("juedou")) {
                //此处条件是故意设置得与八卦阵条件不一样的，虽然感觉没啥用
                if (hasCard(entity, s -> s.isOf(ModItems.SHAN)) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    shan(entity, false, source, amount);
                    return true;
                }
            }
        }
        //4.最低优先度执行：绝情
        return execute(entity, source, amount, list, 4);
        //吐槽：别看这里写了那么多，一旦有一个return true，就没事了
    }

    public static void shan(LivingEntity entity, boolean bl, DamageSource source, float amount) {
        ItemStack stack = bl ? ItemStack.EMPTY : getCard(entity, s -> s.isOf(ModItems.SHAN)).getRight();
        int cd = bl ? 60 : 40;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        if (bl) voice(entity, Sounds.BAGUA);
        voice(entity, Sounds.SHAN);
        nonPreUseCardDecrement(entity, stack, null);
        if (entity instanceof PlayerEntity player) {
            writeDamage(source, amount, !bl, trinketItem(ModItems.CARD_PILE, player));
            if (bl) player.sendMessage(Text.translatable("dabaosword.bagua"),true);
        }
        //虽然没有因为杀而触发闪，但如果攻击者的杀处于自动触发状态，则仍会消耗
        if (source.getSource() instanceof LivingEntity SE && hasItem(SE, isSha)) {
            ItemStack sha = isSha.test(SE.getMainHandStack()) ? SE.getMainHandStack() : getItem(SE, isSha);
            if (sha.isOf(ModItems.SHA)) voice(SE, Sounds.SHA);
            if (sha.isOf(ModItems.FIRE_SHA)) voice(SE, Sounds.SHA_FIRE);
            if (sha.isOf(ModItems.THUNDER_SHA)) voice(SE, Sounds.SHA_THUNDER);
            nonPreUseCardDecrement(SE, sha, entity);
        }
    }
}
