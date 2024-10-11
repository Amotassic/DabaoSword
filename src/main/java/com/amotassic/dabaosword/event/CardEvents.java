package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents implements CardCBs.PostUse, CardCBs.Discard, CardCBs.Move, CardCBs.PreUse {
    final List<Item> triggerWuxie = new ArrayList<>(Arrays.asList(ModItems.BINGLIANG_ITEM, ModItems.DISCARD, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.STEAL, ModItems.TIESUO, ModItems.TOO_HAPPY_ITEM));
    boolean canTrigger(ItemStack stack) {
        for (var i : triggerWuxie) {if (stack.isOf(i)) return true;}
        return false;
    }

    @Override
    public boolean cardUsePre(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        ItemStack card = stack.copy();
        //不论如何先消耗一张卡牌再说，除了拆顺
        if (!card.isOf(ModItems.STEAL) && !card.isOf(ModItems.DISCARD)) cardUseAndDecrement(user, stack);

        if (canTrigger(card) && hasCard(target, s -> s.isOf(ModItems.WUXIE))) {
            cardUsePre(target, new ItemStack(ModItems.WUXIE), null); //递归触发无懈，因此不用再写消耗和执行效果
            if (card.isOf(ModItems.STEAL) || card.isOf(ModItems.DISCARD)) cardUseAndDecrement(user, stack); //补充一个拆顺的消耗，别出bug了
            return false;
        }
        if (isCard(card)) ((Card) card.getItem()).cardUse(user, card, target); //如果卡牌没有被抵消就执行效果
        return true;
    }

    @Override
    public void cardUsePost(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        if (user instanceof PlayerEntity player) {
            //集智技能触发
            if (hasTrinket(SkillCards.JIZHI, player) && stack.isIn(Tags.Items.ARMOURY_CARD)) {
                draw(player);
                voice(player, Sounds.JIZHI);
            }

            //奔袭技能触发
            if (hasTrinket(SkillCards.BENXI, player)) {
                ItemStack trinketItem = trinketItem(SkillCards.BENXI, player);
                int benxi = getTag(trinketItem);
                if (benxi < 5) {
                    setTag(trinketItem, benxi + 1);
                    voice(player, Sounds.BENXI);
                }
            }

            if (hasTrinket(SkillCards.LIANYING, player) && countCards(player) == 0) lianyingTrigger(player);
        }
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
