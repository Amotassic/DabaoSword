package com.amotassic.dabaosword.api.event;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.ICardEvent;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents {
    /**当卡牌使用时触发，用于判断是否能执行卡牌的效果。自动触发的卡牌不会触发该事件，因此还需要用{@link CardEvents#cardUsePost(LivingEntity, ItemStack, LivingEntity)}移除卡牌
     * @param stack 必须传入原始的stack
     * @return true 卡牌能生效，false 卡牌不能生效  注意：若卡牌不生效，需要根据需求来决定是否移除卡牌*/
    public static boolean cardUsePre(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        if (canUse(user, stack, target)) {
            Card card = (Card) stack.getItem();
            card.cardUse(user, stack, target);
            //如果卡牌可以立即生效，则直接触发卡牌使用后事件
            if (!card.notImmediatelyEffective()) cardUsePost(user, stack, target);
            return true;
        }
        return false;
    }

    private static boolean canUse(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        if (target != null) {
            for (var skill : allTrinkets(target)) {
                if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, target)) {
                    boolean canUse = e.canUseIfTargetHasSkill(user, stack, target, skill);
                    if (!canUse) {cardUsePost(user, stack, target); return false;}
                }
            }

            if (stack.isIn(Tags.Items.TRIGGER_WUXIE) && hasCard(target, p(ModItems.WUXIE))) {
                cardUsePre(target, new ItemStack(ModItems.WUXIE), null);
                cardUsePost(user, stack, target);
                return false;
            }
        }
        return true;
    }

    /**当使用的卡牌（包括自动触发）结算完成后触发*/
    public static void cardUsePost(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        cardUsePost(user, stack, target, true);
    }
    public static void cardUsePost(LivingEntity user, ItemStack stack, @Nullable LivingEntity target, boolean consume) {
        if (stack.getItem() instanceof CardItem) voice(user, stack);
        ItemStack copy = stack.copy();
        if (consume) cardUseAndDecrement(user, copy); //todo 这里是否需要copy？
        for (var skill : allTrinkets(user)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, user)) e.postCardUse(user, copy, target, skill);
        }
    }

    /**调用卡牌弃置监听器的方法，除非stack来自牌堆背包，否则一定要传入原始的stack！
     * 因为还没有真正到事件触发时就已经移除了卡牌，所以事件中使用的stack是复制的！*/
    public static void cardDiscard(LivingEntity entity, ItemStack stack, int count, boolean fromEquip) {
        ItemStack copy = stack.copyWithCount(count);
        for (var skill : allTrinkets(entity)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, entity)) {
                boolean shouldDiscard = e.shouldDiscard(entity, copy, count, fromEquip, skill);
                if (!shouldDiscard) return;
            }
        }
        //移除被弃置的牌
        cardDecrement(entity, stack, count);
        XingshangTrigger(entity, copy);
        for (var skill : allTrinkets(entity)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, entity)) e.onCardDiscard(entity, copy, count, fromEquip, skill);
        }
    }

    private static void XingshangTrigger(LivingEntity entity, ItemStack stack) {
        if (entity.isAlive()) return;
        for (PlayerEntity player : entity.getWorld().getPlayers()) {
            if (hasTrinket(SkillCards.XINGSHANG, player) && player.distanceTo(entity) <= 25 && player != entity) {
                if (!player.getCommandTags().contains("xingshang")) voice(player, Sounds.XINGSHANG);
                player.addCommandTag("xingshang"); //防止同时触发大量语音播放
                give(player, stack.copy());
                return;
            }
        }
    }

    /**调用卡牌移动监听器的方法，除非stack来自牌堆背包，否则一定要传入原始的stack！
     * 因为还没有真正到事件触发时就已经移除了卡牌，所以事件中使用的stack是复制的！*/
    public static void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, boolean fromEquip, boolean toEquip) {
        ItemStack copy = stack.copyWithCount(count);
        //移除来源的牌
        cardDecrement(from, stack, count);
        //如果是移动到装备栏，则目标使用或替换该装备，否则直接给to等量物品
        if (toEquip) Equipment.useOrReplaceEquip(to, copy); else give(to, copy);
        for (var skill : allTrinkets(from)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, from)) e.onCardMove(from, skill, to, copy, count, fromEquip, toEquip);
        }
    }

    public static boolean notHurtBy(LivingEntity e, DamageSource s, Item c) {return !canHurtByCard(e,s,new ItemStack(c));}
    public static boolean canHurtByCard(LivingEntity entity, DamageSource source, ItemStack card) {
        for (var skill : allTrinkets(entity)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, entity)) {
                boolean canHurt = e.canHurtByCard(entity, skill, card, source);
                if (!canHurt) return false;
            }
        }
        return true;
    }

    public static void hurtBy(LivingEntity e, DamageSource s, Item c) {hurtByCard(e, s, new ItemStack(c));}
    public static void hurtByCard(LivingEntity entity, DamageSource source, ItemStack card) {
        for (var skill : allTrinkets(entity)) {
            if (skill.getItem() instanceof ICardEvent e && canTrigger(skill, entity)) e.onHurtByCard(entity, skill, card, source);
        }
    }


    /**专为处理卡牌减少而写的方法，牌堆中的卡牌减少，需要保存nbt*/
    public static void cardDecrement(Pair<CardPileInventory, ItemStack> stack, int count) {
        if (stack.getLeft() == null) stack.getRight().decrement(count);
        else stack.getLeft().removeStack(stack.getRight(), count);
    }
    /**另一个用于处理卡牌减少的方法，暂时只用于卡牌弃置和移动的方法中*/
    public static void cardDecrement(LivingEntity entity, ItemStack stack, int count) {
        var pair = getCard(entity, s -> ItemStack.areEqual(s, stack));
        //如果是牌堆中的卡牌，需要调用牌堆的方法来减少，以保存nbt
        if (pair.getLeft() != null) pair.getLeft().removeStack(pair.getRight(), count);
        //那么为什么这里没有else呢？因为此stack非彼pair.getRight()，如果不减少会出bug
        stack.decrement(count);
    }
    /**卡牌使用后减少，不需要传入原始的itemStack*/
    public static void cardUseAndDecrement(LivingEntity user, ItemStack card) {
        //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
        if (card.isOf(ModItems.WUXIE)) cardDecrement(getCard(user, p(ModItems.WUXIE)), 1);
        else {
            //如果使用者是创造模式玩家，则不消耗卡牌
            if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) return;
            //找到和要消耗的完全相同的卡牌，若找不到，则找和要消耗的卡牌同名的牌
            var pair = getCard(user, s -> ItemStack.areEqual(s, card));
            if (pair.getRight().isEmpty()) pair = getCard(user, p(card.getItem()));
            //如果使用者是玩家，且即将消耗的卡牌stack数量为1，则尝试补充卡牌
            if (user instanceof PlayerEntity player && pair.getRight().getCount() == 1) {
                if (isSha.test(card)) { //如果使用了杀则补充杀，否则补充同名牌
                    cardDecrement(pair, 1);
                    var p = getCard(user, isSha); var s = p.getRight();
                    if (!s.isEmpty()) {give(player, s.copy()); cardDecrement(p, s.getCount());}
                } else if (ItemStack.areEqual(pair.getRight(), user.getMainHandStack())) {
                    cardDecrement(pair, 1);
                    var p = getCard(user, p(card.getItem())); var s = p.getRight();
                    if (!s.isEmpty()) {player.setStackInHand(Hand.MAIN_HAND, s.copy()); cardDecrement(p, s.getCount());}
                } else cardDecrement(pair, 1);
            } else cardDecrement(pair, 1);
        }
    }
}
