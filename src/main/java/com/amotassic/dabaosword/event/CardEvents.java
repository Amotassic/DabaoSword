package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents implements CardCBs.PostUse, CardCBs.Discard, CardCBs.Move {
    @Override
    public void cardUsePost(PlayerEntity user, Pair<CardPileInventory, ItemStack> stack, @Nullable LivingEntity target) {
        ItemStack copy = stack.getRight().copy();

        if (stack.getRight().isOf(ModItems.WUXIE)) cardDecrement(stack, 1); //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
        else if (!user.isCreative()) cardDecrement(stack, 1);

        if (user.getMainHandStack().isEmpty() && stack.getRight().isEmpty() && stack.getLeft() == null) { //使用主手上的牌之后自动补牌
            Pair<CardPileInventory, ItemStack> next;
            Predicate<ItemStack> sameItem = s -> s.getItem() == copy.getItem();
            Predicate<ItemStack> nonEquip = s -> s.isIn(Tags.Items.BASIC_CARD) && s.isIn(Tags.Items.ARMOURY_CARD);
            if (hasCard(user, sameItem)) next = getCard(user, sameItem);        //先检索同名非装备牌
            else if (hasCard(user, nonEquip)) next = getCard(user, nonEquip);   //再检索非装备牌
            else next = getCard(user, isCard);                                  //最后随便选一张牌

            if (!next.getRight().isEmpty()) {
                user.setStackInHand(Hand.MAIN_HAND, next.getRight().copy());
                cardDecrement(next, next.getRight().getCount());
            }
        } //todo 有bug的代码，暂时不知道原因

        //集智技能触发
        if (hasTrinket(SkillCards.JIZHI, user) && copy.isIn(Tags.Items.ARMOURY_CARD)) {
            draw(user);
            voice(user, Sounds.JIZHI);
        }

        //奔袭技能触发
        if (hasTrinket(SkillCards.BENXI, user)) {
            ItemStack trinketItem = trinketItem(SkillCards.BENXI, user);
            int benxi = getTag(trinketItem);
            if (benxi < 5) {
                setTag(trinketItem, benxi + 1);
                voice(user, Sounds.BENXI);
            }
        }

        if (hasTrinket(SkillCards.LIANYING, user) && countCards(user) == 0) lianyingTrigger(user);
    }

    @Override
    public void cardDiscard(PlayerEntity player, Pair<CardPileInventory, ItemStack> stack, int count, boolean fromEquip) {
        //移除被弃置的牌
        cardDecrement(stack, count);

        //弃置牌后，玩家的死亡判断是有必要的
        if (player.isAlive()) {
            if (hasTrinket(SkillCards.LIANYING, player) && !fromEquip && countCards(player) == 0) lianyingTrigger(player);

            if (hasTrinket(SkillCards.XIAOJI, player) && fromEquip) xiaojiTrigger(player);
        }
    }

    @Override
    public void cardMove(LivingEntity from, PlayerEntity to, Pair<CardPileInventory, ItemStack> stack, int count, CardCBs.T type) {
        ItemStack copy = stack.getRight().copyWithCount(count);

        //如果是移动到物品栏的类型，则减少from的物品，给to等量的物品（移动到装备区有专门的方法）
        if (type == CardCBs.T.INV_TO_INV || type == CardCBs.T.EQUIP_TO_INV) {
            give(to, copy);
            cardDecrement(stack, count);
        }

        if (type == CardCBs.T.INV_TO_EQUIP || type == CardCBs.T.INV_TO_INV) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.LIANYING, player) && countCards(player) == 0) lianyingTrigger(player);
        }

        if (type == CardCBs.T.EQUIP_TO_INV || type == CardCBs.T.EQUIP_TO_EQUIP) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.XIAOJI, player)) xiaojiTrigger(player);
        }
    }

    private void lianyingTrigger(PlayerEntity player) {
        ItemStack stack = trinketItem(SkillCards.LIANYING, player);
        if (stack != null) setCD(stack, 5);
    }

    private void xiaojiTrigger(PlayerEntity player) {
        draw(player, 2);
        voice(player, Sounds.XIAOJI);
    }
}
