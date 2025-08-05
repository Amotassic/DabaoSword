package com.amotassic.dabaosword.api;

import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.equipment.Equipment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents {

    public static void cardDiscard(LivingEntity entity, ExData exData) {
        exData.forEachCard(1, (c, i) ->
                getResult(Trigger.SHOULD_DISCARD, entity, entity, d().cards(c, i)) > 0);
        exData.forEachCard(2, (c, i) ->
                getResult(Trigger.SHOULD_DISCARD, entity, entity, d().cards(c, i, true)) > 0);
        exData.forEachCard(3, (c, i) -> {cardDecrement(entity, c.origin(), i);});
        getSkillOwners(entity).forEach(player -> getResult(Trigger.LOSE_CARD_DISCARD, player, entity, exData));
    }

    public static void cardMove(LivingEntity from, ExData exData, LivingEntity to) {
        exData.forEachCard(3, (c, i) -> {
            cardDecrement(from, c.origin(), i);
            give(to, c.toStack().copyWithCount(i));
        });
        getSkillOwners(from).forEach(player -> {
            getResult(Trigger.LOSE_CARD_MOVE, player, from, exData);
            getResult(Trigger.GET_CARD_MOVE, player, to, exData);
        });
    }

    public static void cardToEquip(LivingEntity from, ExData exData, LivingEntity to) {
        exData.forEachCard(3, (c, i) -> {
            cardDecrement(from, c.origin(), i);
            Equipment.useOrReplaceEquip(to, c.toStack().copyWithCount(i));
        });
        getSkillOwners(from).forEach(player -> getResult(Trigger.LOSE_CARD_MOVE, player, from, exData));
    }

    public static void hurtByCard(LivingEntity entity, DamageSource source, float amount) {
        Item card = switch (source.getType().msgId()) {
            case "huogong" -> ModItems.FIRE_ATTACK;
            case "juedou" -> ModItems.JUEDOU;
            case "nanman" -> ModItems.NANMAN;
            case "wanjian" -> ModItems.WANJIAN;
            case "shandian" -> ModItems.SHANDIAN_ITEM;
            case "sha" -> ModItems.SHA;
            case "shaFire" -> ModItems.FIRE_SHA;
            case "shaThunder" -> ModItems.THUNDER_SHA;
            default -> ItemStack.EMPTY.getItem();
        };
        getSkillOwners(entity).forEach(player ->
                getResult(Trigger.HURT_BY_CARD, player, entity, d().cards(newCard(card), 1).withDamage(source, amount)));
    }

    /**专为处理卡牌减少而写的方法，牌堆中的卡牌减少，需要保存nbt*/
    public static void cardDecrement(LivingEntity entity, ItemStack stack, int count) {
        if (entity instanceof PlayerEntity player) {
            if (getCardPack(player).removeStack(stack, count)) return;
            else stack.decrement(count);
            return;
        }
        stack.decrement(count);
    }
    /**卡牌使用后减少，不需要传入原始的itemStack*/
    public static void cardUseAndDecrement(LivingEntity user, ItemStack card) {
        //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
        if (card.isOf(ModItems.WUXIE)) cardDecrement(user, getCard(user, p(ModItems.WUXIE)), 1);
        else {
            //如果使用者是创造模式玩家，则不消耗卡牌
            if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) return;
            Item usedItem = card.getItem();
            //找到和要消耗的完全相同的卡牌，若找不到，则找和要消耗的卡牌同名的牌
            var stack = getCard(user, s -> ItemStack.areEqual(s, card));
            if (stack.isEmpty()) stack = getCard(user, p(usedItem));
            cardDecrement(user, stack, 1);
            //如果使用者是玩家，且消耗的卡牌stack没了，则尝试补充卡牌
            if (user instanceof PlayerEntity player && stack.getCount() == 0) {
                if (isSha.test(usedItem.getDefaultStack())) { //如果使用了杀则补充杀，否则补充同名牌
                    var s = getCard(user, isSha);
                    if (!s.isEmpty()) {give(player, s.copy()); cardDecrement(player, s, s.getCount());}
                } else if (player.getMainHandStack().isEmpty()) {
                    var s = getCard(user, p(usedItem));
                    if (!s.isEmpty()) {player.setStackInHand(Hand.MAIN_HAND, s.copy()); cardDecrement(player, s, s.getCount());}
                }
            }
        }
    }
}
