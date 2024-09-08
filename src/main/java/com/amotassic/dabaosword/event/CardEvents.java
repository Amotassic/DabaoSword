package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardMoveCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents implements CardUsePostCallback, CardDiscardCallback, CardMoveCallback {
    @Override
    public void cardUsePost(PlayerEntity user, ItemStack stack, @Nullable LivingEntity target) {
        ItemStack copy = stack.copy();

        if (stack.getItem() == ModItems.WUXIE) stack.decrement(1); //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
        else if (!user.isCreative()) stack.decrement(1);

        //集智技能触发
        if (hasTrinket(SkillCards.JIZHI, user) && copy.isIn(Tags.Items.ARMOURY_CARD)) {
            draw(user, 1);
            if (new Random().nextFloat() < 0.5) {voice(user, Sounds.JIZHI1);} else {voice(user, Sounds.JIZHI2);}
        }

        //奔袭技能触发
        if (hasTrinket(SkillCards.BENXI, user)) {
            ItemStack trinketItem = trinketItem(SkillCards.BENXI, user);
            int benxi = getTag(trinketItem);
            if (benxi < 5) {
                benxi ++; setTag(trinketItem, benxi);
                if (new Random().nextFloat() < 0.5) {voice(user, Sounds.BENXI1);} else {voice(user, Sounds.BENXI2);}
            }
        }

        if (hasTrinket(SkillCards.LIANYING, user) && countAllCard(user) == 0) lianyingTrigger(user);
    }

    @Override
    public void cardDiscard(PlayerEntity player, ItemStack stack, int count, boolean fromEquip) {
        //移除被弃置的牌
        stack.decrement(count);

        //弃置牌后，玩家的死亡判断是有必要的
        if (player.isAlive()) {
            if (hasTrinket(SkillCards.LIANYING, player) && !fromEquip && countAllCard(player) == 0) lianyingTrigger(player);

            if (hasTrinket(SkillCards.XIAOJI, player) && fromEquip) xiaojiTrigger(player);
        }
    }

    @Override
    public void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, Type type) {
        ItemStack copy = stack.copyWithCount(count);

        //如果是移动到物品栏的类型，则减少from的物品，给to等量的物品（移动到装备区有专门的方法）
        if (type == Type.INV_TO_INV || type == Type.EQUIP_TO_INV) {
            give(to, copy);
            stack.decrement(count);
        }

        if (type == Type.INV_TO_EQUIP || type == Type.INV_TO_INV) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.LIANYING, player) && countAllCard(player) == 0) lianyingTrigger(player);
        }

        if (type == Type.EQUIP_TO_INV || type == Type.EQUIP_TO_EQUIP) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.XIAOJI, player)) xiaojiTrigger(player);
        }
    }

    private void lianyingTrigger(PlayerEntity player) {
        ItemStack stack = trinketItem(SkillCards.LIANYING, player);
        if (stack != null) setCD(stack, 5);
    }

    private void xiaojiTrigger(PlayerEntity player) {
        give(player, new ItemStack(ModItems.GAIN_CARD, 2));
        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.XIAOJI1);} else {voice(player, Sounds.XIAOJI2);}
    }
}
