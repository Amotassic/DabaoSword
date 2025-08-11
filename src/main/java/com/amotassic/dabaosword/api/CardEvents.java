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

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents {

    /**
     * 装备牌被弃置前触发的事件，可以理解为装备牌的“亡语”，典型比如白银狮子回血效果。
     * 由于我设计的是卡牌被清除后才会触发技能效果，如果被清除的装备牌本身有技能效果，那就无法触发了，因此补充一个触发时机。
     * 由于这个事件不会取消后续事件的触发，所以不需要返回值.
     * @param owner 装备牌的拥有者
     */
    private static void triggerCardSkillBeforeDiscard(LivingEntity owner, ExData exData, Trigger t) {
        exData.forEachCard(2, (c, i) -> {
            ItemStack stack = c.toStack();
            if (stack.getItem() instanceof Equipment) {
                var skill = s(stack);
                for (var data : skill.data()) {
                    if (!List.of(data.trigger()).contains(t) || !data.relation().test(owner, owner, exData.source)) continue;
                    data.apply(owner, owner, skill, exData);
                }
            }
        });
    }

    /**
     * 卡牌被弃置，包括发动技能弃牌，被过河拆桥弃牌，替换装备时弃置旧的装备等情况
     * @param exData 包含被弃置的卡牌数据
     */
    public static void cardDiscard(LivingEntity entity, ExData exData) {
        exData.forEachCard(1, (c, i) ->
                getResult(Trigger.SHOULD_DISCARD, entity, entity, d().cards(c, i)) > 0);
        exData.forEachCard(2, (c, i) ->
                getResult(Trigger.SHOULD_DISCARD, entity, entity, d().cards(c, i, true)) > 0);
        triggerCardSkillBeforeDiscard(entity, exData, Trigger.LOSE_CARD_DISCARD);
        exData.forEachCard(3, (c, i) -> {cardDecrement(entity, c.origin(), i);});
        getSkillOwners(entity).forEach(player -> getResult(Trigger.LOSE_CARD_DISCARD, player, entity, exData));
    }

    /**
     * 卡牌从一个生物的区域移动到另一个生物的非装备区域，比如顺手牵羊，仁德给牌等操作
     * @param exData 包含被移动的卡牌数据
     */
    public static void cardMove(LivingEntity from, ExData exData, LivingEntity to) {
        triggerCardSkillBeforeDiscard(from, exData, Trigger.LOSE_CARD_MOVE);
        exData.forEachCard(3, (c, i) -> {
            cardDecrement(from, c.origin(), i);
            give(to, c.toStack().copyWithCount(i));
        });
        getSkillOwners(from).forEach(player -> {
            getResult(Trigger.LOSE_CARD_MOVE, player, from, exData);
            getResult(Trigger.GET_CARD_MOVE, player, to, exData);
        });
    }

    /**
     * 卡牌从一个生物的区域移动到另一个生物的装备区，比如直谏技能的效果
     * @param exData 包含被移动的卡牌数据
     */
    public static void cardToEquip(LivingEntity from, ExData exData, LivingEntity to) {
        triggerCardSkillBeforeDiscard(from, exData, Trigger.LOSE_CARD_MOVE);
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
    public static void cardUseAndDecrement(LivingEntity user, ItemStack card, Hand hand) {
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
            if (user instanceof PlayerEntity player && stack.isEmpty()) {
                if (isSha.test(usedItem.getDefaultStack())) { //如果使用了杀则补充杀，否则补充同名牌
                    var s = getCard(user, isSha);
                    if (!s.isEmpty()) {
                        if (hand != null) player.setStackInHand(hand, s.copy()); else give(player, s.copy());
                        cardDecrement(player, s, s.getCount());
                    }
                } else if (hand != null && player.getStackInHand(hand).isEmpty()) {
                    var s = getCard(user, p(usedItem));
                    if (!s.isEmpty()) {player.setStackInHand(hand, s.copy()); cardDecrement(player, s, s.getCount());}
                }
            }
        }
    }
}
