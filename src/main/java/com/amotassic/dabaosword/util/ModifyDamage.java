package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.ISha;
import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.amotassic.dabaosword.api.event.CardEvents.*;
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
            pairList.add(calculateDMG(entity, source, value, SE));

            //插入一个武器版古锭刀的结算
            int i = 0; //i == 4则说明受击者的盔甲栏没有任何物品
            for (var s : entity.getArmorItems()) {if (s.isEmpty()) i++;}
            if (i == 4 && SE.getMainHandStack().isOf(ModItems.GUDINGDAO)) multiply += 1;
            //这里的这个else很重要！防止两个条件同时满足时会触发双重结算
        } else if (source.getAttacker() instanceof LivingEntity AT) pairList.add(calculateDMG(entity, source, value, AT));
        for (var p : pairList) {
            multiply += p.getLeft().getLeft();
            add += p.getLeft().getRight();
            reducing.addAll(p.getRight());
        }

        //伤害结算
        value = value * (1 + multiply) + add;
        for (var f : reducing) {value *= (1 + f);}
        return value;
    }

    private static Pair<Pair<Float, Float>, List<Float>> calculateDMG(LivingEntity entity, DamageSource source, float value, LivingEntity trinketOwner) {
        float m = 0; float a = 0;
        List<Float> r = new ArrayList<>();
        for (var stack : allTrinkets(trinketOwner)) {
            Pair<Float, Float> fp = null;
            if (stack.getItem() instanceof Skill skill) fp = skill.modifyDamage(entity, source, value);

            if (fp == null) continue;
            if (fp.getLeft() < 0) r.add(fp.getLeft()); else m += fp.getLeft();
            a += fp.getRight();
        }
        return new Pair<>(new Pair<>(m, a), r);
    }

    private static List<List<ItemStack>> eventStacks(LivingEntity entity, DamageSource source, float value, LivingEntity trinketOwner) {
        List<List<ItemStack>> stacks = new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        for (var stack : allTrinkets(trinketOwner)) {
            Skill.Priority priority = null;
            if (stack.getItem() instanceof Skill skill) priority = skill.getPriority(entity, source, value);

            if (priority != null) stacks.get(priority.ordinal()).add(stack);
        }
        return stacks;
    }

    private static boolean execute(LivingEntity entity, DamageSource source, float amount, List<List<ItemStack>> list, int index) {
        for (var s : list.get(index)) {
            if (s.getItem() instanceof Skill skill && skill.cancelDamage(entity, source, amount)) return true;
        }
        return false;
    }

    /**取消伤害结算：先获取所有输出了结算优先度的ItemStack，将它们按照优先度依次排列（见eventStacks方法），再分别结算（见execute方法）
     * @return 0表示不取消伤害，1表示取消伤害以及后续处理，2表示仅取消伤害，但可以执行后续处理*/
    public static int shouldCancel(LivingEntity entity, DamageSource source, float amount) {
        Entity so = source.getSource(); Entity at = source.getAttacker();
        //检查事件优先度，获取输出了优先度的stack
        List<List<ItemStack>> list = eventStacks(entity, source, amount, entity);
        List<List<ItemStack>> l1 = null; List<List<ItemStack>> l2 = null;
        if (so instanceof LivingEntity SE) l1 = eventStacks(entity, source, amount, SE);
        else if (at instanceof LivingEntity AT) l2 = eventStacks(entity, source, amount, AT);
        //合并3个list中的所有优先度和stack
        for (int i = 0; i < 5; i++) {
            if (l1 != null) list.get(i).addAll(l1.get(i));
            if (l2 != null) list.get(i).addAll(l2.get(i));
        }
        //0.最高优先度执行，暂无用途
        if (execute(entity, source, amount, list, 0)) return 1;
        //无敌效果
        if (entity.hasStatusEffect(ModItems.INVULNERABLE) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return 1;

        if (so instanceof LivingEntity SE) {
            //被乐的生物无法造成伤害
            if (SE.hasStatusEffect(ModItems.TOO_HAPPY)) return 1;
            //沈佳宜防御效果
            if (!(SE instanceof PlayerEntity) && entity.hasStatusEffect(ModItems.DEFEND)) {
                if (Objects.requireNonNull(entity.getStatusEffect(ModItems.DEFEND)).getAmplifier() >= 2) return 1;
            }
            //决斗等物品虽然手长，但过远时普通伤害无效
            if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && entity.distanceTo(SE) > 5) {
                if (SE.getMainHandStack().isOf(ModItems.JUEDOU) || SE.getMainHandStack().isOf(ModItems.DISCARD)) return 1;
            }
        } else if (at instanceof LivingEntity AT) {
            //被乐的生物无法造成伤害
            if (AT.hasStatusEffect(ModItems.TOO_HAPPY)) return 1;
        }

        if (isWanjian(source) && notHurtBy(entity, ModItems.WANJIAN)) return 1;
        if (isHuogong(source) && notHurtBy(entity, ModItems.FIRE_ATTACK)) return 1;
        if (isShandian(source) && notHurtBy(entity, ModItems.SHANDIAN_ITEM)) return 1;
        if (so instanceof LivingEntity SE && shouldSha(SE)) { //只要能触发杀，伤害就会被取消
            ItemStack sha = isSha.test(SE.getMainHandStack()) ? SE.getMainHandStack() : getItem(SE, isSha);
            SE.addCommandTag("sha");
            if (canHurtByCard(entity, sha)) {
                ISha iSha = (ISha) sha.getItem();
                if (iSha.sha(SE, entity, amount)) iSha.shaEffect(SE, entity, sha);
                else { //如果杀被无效化了，就会尝试触发贯石斧的效果
                    var guanshi = trinketItem(ModItems.GUANSHI, SE);
                    if (!guanshi.isEmpty() && getCD(guanshi) == 0 && entity.hasStatusEffect(ModItems.INVULNERABLE)) {
                        setCD(guanshi, 10); voice(SE, guanshi);
                        entity.removeStatusEffect(ModItems.INVULNERABLE);
                        if (iSha.sha(SE, entity, amount)) iSha.shaEffect(SE, entity, sha);
                    }
                }
            }
            cardUsePost(SE, sha, entity);
            return 2;
        }

        //1.高优先度执行：装备
        if (execute(entity, source, amount, list, 1)) return 1;
        //2.一般优先度执行：技能
        if (execute(entity, source, amount, list, 2)) return 1;
        //3.低优先度执行：卡牌 闪以及响应南蛮的杀
        if (execute(entity, source, amount, list, 3)) return 1;
        if (at != null && at.getCommandTags().contains("nanman")) {
            var stack = getCard(entity, isSha);
            if (!stack.isEmpty()) {
                voice(entity, stack);
                cardUsePost(entity, stack, null);
                entity.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 2, 0, false, false, false)); //防止被其他南蛮入侵召唤物误伤
                return 1;
            }
        }
        if (at instanceof LivingEntity) {
            if (!entity.hasStatusEffect(ModItems.COOLDOWN2)) {
                //此处条件是故意设置得与八卦阵条件不一样的，虽然感觉没啥用
                if (hasCard(entity, p(ModItems.SHAN)) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    shan(entity, false, source, amount);
                    return 1;
                }
            }
        }
        //4.最低优先度执行：绝情
        if (execute(entity, source, amount, list, 4)) return 1;
        return 0;
    }

    private static boolean shouldSha(LivingEntity entity) {
        return hasItem(entity, isSha) && !entity.getCommandTags().contains("sha") && !entity.getCommandTags().contains("juedou") && !entity.getCommandTags().contains("nanman");
    }

    public static void shan(LivingEntity entity, boolean bl, DamageSource source, float amount) {
        ItemStack stack = new ItemStack(ModItems.SHAN);
        int cd = bl ? 60 : 40;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        if (bl) voice(entity, Sounds.BAGUA);
        cardUsePost(entity, stack, null, !bl); //如果触发八卦阵，就不用移除闪了
        if (entity instanceof PlayerEntity player) {
            writeDamage(source, amount, !bl, trinketItem(ModItems.CARD_PILE, player));
            if (bl) player.sendMessage(Text.translatable("dabaosword.bagua"),true);
        }
    }
}
