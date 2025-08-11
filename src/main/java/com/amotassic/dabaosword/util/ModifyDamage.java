package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.WarmWineItem;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.card.Sha;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ModifyDamage {
    //由于fabric没有提供修改伤害的方法，因此我自己注入"applyArmorToDamage"以修改生物受到的伤害值
    public static float modify(LivingEntity entity, DamageSource source, float value) {
        float multiply = 0; //倍率增伤乘区
        float add = 0; //固定数值加减伤害区
        List<Float> reducing = new ArrayList<>(); //最终减伤乘区，存储负值，每个值使最终伤害为原来的（1+x）倍

        var exData = d().withDamage(source, value);
        getSkillOwners(entity).forEach(player -> getResult(Trigger.MODIFY_DAMAGE, player, entity, exData));

        for (Float f : exData.adds) add += f;
        for (Float f : exData.muls) {
            if (f < 0) reducing.add(f); else multiply += f;
        }

        if (source.getSource() instanceof LivingEntity SE && SE.getMainHandStack().isOf(ModItems.GUDINGDAO)) {
            //插入一个武器版古锭刀的结算
            int i = 0; //i == 4则说明受击者的盔甲栏没有任何物品
            for (var s : getArmorItems(entity)) {if (s.isEmpty()) i++;}
            if (i == 4) multiply += 1;
        }

        //伤害结算
        value = value * (1 + multiply) + add;
        for (var f : reducing) {value *= (1 + f);}
        return value;
    }

    /**取消伤害结算：
     * @return 0表示不取消伤害，1表示取消伤害以及后续处理，2表示仅取消伤害，但可以执行后续处理*/
    public static int shouldCancel(LivingEntity entity, DamageSource source, float amount) {
        List<LivingEntity> owners = getSkillOwners(entity);
        var exData = d().withDamage(source, amount);

        Entity so = source.getSource(); Entity at = source.getAttacker();
        //0.最高优先度执行，暂无用途
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_HIGHEST, e, entity, exData);
            if (i > 0) return i;
        }
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
            if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && shouldReachLong(SE) && entity.distanceTo(SE) > 5) return 1;
        } else if (at instanceof LivingEntity AT) {
            //被乐的生物无法造成伤害
            if (AT.hasStatusEffect(ModItems.TOO_HAPPY)) return 1;
        }

        if (so instanceof LivingEntity SE && shouldSha(SE)) { //只要能触发杀，伤害就会被取消
            Hand hand = isSha.test(SE.getMainHandStack()) ? Hand.MAIN_HAND : isSha.test(SE.getOffHandStack()) ? Hand.OFF_HAND : null;
            ItemStack sha = hand != null ? SE.getStackInHand(hand) : getItem(SE, isSha);
            SE.addCommandTag("sha");
            Sha.shaUse(SE, sha, hand, amount, entity);
            return 2;
        }

        //1.高优先度执行：装备
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_HIGH, e, entity, exData);
            if (i > 0) return i;
        }
        //2.一般优先度执行：技能
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_NORMAL, e, entity, exData);
            if (i > 0) return i;
        }
        //3.低优先度执行：卡牌 闪以及响应南蛮的杀
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_LOW, e, entity, exData);
            if (i > 0) return i;
        }
        if (at != null && at.getCommandTags().contains("nanman")) {
            var stack = getCard(entity, isSha);
            if (!stack.isEmpty()) {
                voice(entity, stack);
                CardItem.onUse(entity, stack, null, true);
                return 1;
            }
        }
        if (at instanceof LivingEntity) {
            if (!entity.hasStatusEffect(ModItems.COOLDOWN2)) {
                //此处条件是故意设置得与八卦阵条件不一样的，虽然感觉没啥用
                if (hasCard(entity, p(ModItems.SHAN)) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    ItemStack shan = new ItemStack(ModItems.SHAN);
                    CardItem.onUse(entity, shan, null, true);
                    shan(entity, false, source, amount);
                    return 1;
                }
            }
        }
        //4.最低优先度执行：绝情
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_LOWEST, e, entity, exData);
            if (i > 0) return i;
        }
        return 0;
    }

    private static boolean shouldSha(LivingEntity entity) {
        return hasItem(entity, isSha) && !entity.getCommandTags().contains("sha") && !entity.getCommandTags().contains("juedou") && !entity.getCommandTags().contains("nanman");
    }

    public static void shan(LivingEntity entity, boolean bl, DamageSource source, float amount) {
        int cd = bl ? 60 : 40;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        if (entity instanceof PlayerEntity player) {
            writeDamage(source, amount, !bl, trinketItem(ModItems.CARD_PILE, player));
            if (bl) player.sendMessage(Text.translatable("dabaosword.bagua"),true);
        }
    }

    public static ItemStack modifyStack(ItemStack stack) {
        if (stack.isOf(ModItems.SUNSHINE_SMILE)) {
            stack.addEnchantment(ModTools.getEntry(ModItems.CRIT), 1);
        }
        return stack;
    }

    public static boolean warmWine(LivingEntity entity, DamageSource source) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (source.getAttacker() instanceof PlayerEntity player && !player.getCommandTags().contains("sha")) {
                ItemStack wine = getItem(player, p(ModItems.WARM_WINE));
                if (wine.isEmpty()) return false;
                player.addCommandTag("sha"); wine.decrement(1);
                if (!player.isCreative() && !player.isSpectator()) give(player, newCard(ModItems.JIU));

                ItemStack head = WarmWineItem.dropHead(entity);
                if (!head.isEmpty()) entity.dropStack(world, head);
                entity.damage(world, damageSource(player, DamageTypes.GENERIC_KILL), entity.getMaxHealth() * 100);
                entity.kill(world);
                return true;
            }
        }
        return false;
    }
}
