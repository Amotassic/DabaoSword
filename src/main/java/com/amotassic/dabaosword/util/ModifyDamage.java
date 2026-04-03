package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.WarmWineItem;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.card.Sha;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

        if (source.getDirectEntity() instanceof LivingEntity SE && SE.getMainHandItem().is(ModItems.GUDINGDAO)) {
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

        Entity so = source.getDirectEntity(); Entity at = source.getEntity();
        //0.最高优先度执行，暂无用途
        for (LivingEntity e : owners) {
            int i = getResult(Trigger.CANCEL_DAMAGE_HIGHEST, e, entity, exData);
            if (i > 0) return i;
        }
        //无敌效果
        if (entity.hasEffect(ModItems.INVULNERABLE) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return 1;

        if (so instanceof LivingEntity SE) {
            //被乐的生物无法造成伤害
            if (SE.hasEffect(ModItems.TOO_HAPPY)) return 1;
            //沈佳宜防御效果
            if (!(SE instanceof Player) && entity.hasEffect(ModItems.DEFEND)) {
                if (Objects.requireNonNull(entity.getEffect(ModItems.DEFEND)).getAmplifier() >= 2) return 1;
            }
        } else if (at instanceof LivingEntity AT) {
            //被乐的生物无法造成伤害
            if (AT.hasEffect(ModItems.TOO_HAPPY)) return 1;
        }

        if (so instanceof LivingEntity SE && shouldSha(SE)) { //只要能触发杀，伤害就会被取消
            var hand = isSha.test(SE.getMainHandItem()) ? InteractionHand.MAIN_HAND : isSha.test(SE.getOffhandItem()) ? InteractionHand.OFF_HAND : null;
            ItemStack sha = hand != null ? SE.getItemInHand(hand) : getItem(SE, isSha);
            SE.addTag("sha");
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
        if (at != null && at.entityTags().contains("nanman")) {
            var stack = getCard(entity, isSha);
            if (!stack.isEmpty()) {
                voice(entity, stack);
                CardItem.onUse(entity, stack, null, true);
                return 1;
            }
        }
        if (at instanceof LivingEntity) {
            if (!entity.hasEffect(ModItems.COOLDOWN2)) {
                //此处条件是故意设置得与八卦阵条件不一样的，虽然感觉没啥用
                if (hasCard(entity, p(ModItems.SHAN)) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
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
        return hasItem(entity, isSha) && !entity.entityTags().contains("sha") && !entity.entityTags().contains("juedou") && !entity.entityTags().contains("nanman");
    }

    public static void shan(LivingEntity entity, boolean bl, DamageSource source, float amount) {
        int cd = bl ? 60 : 40;
        entity.addEffect(new MobEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        entity.addEffect(new MobEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        if (entity instanceof Player player) {
            writeDamage(source, amount, !bl, trinketItem(ModItems.CARD_PILE, player));
            if (bl) player.sendOverlayMessage(Component.translatable("dabaosword.bagua"));
        }
    }

    public static boolean warmWine(LivingEntity entity, DamageSource source) {
        if (entity.level() instanceof ServerLevel world) {
            if (source.getEntity() instanceof Player player && !player.entityTags().contains("sha")) {
                ItemStack wine = getItem(player, p(ModItems.WARM_WINE));
                if (wine.isEmpty()) return false;
                player.addTag("sha"); wine.shrink(1);
                if (!player.isCreative() && !player.isSpectator()) give(player, newCard(ModItems.JIU));

                ItemStack head = WarmWineItem.dropHead(entity);
                if (!head.isEmpty()) entity.drop(head, false, false);
                entity.hurtServer(world, damageSource(player, DamageTypes.GENERIC_KILL), entity.getMaxHealth() * 100);
                entity.kill(world);
                return true;
            }
        }
        return false;
    }
}
