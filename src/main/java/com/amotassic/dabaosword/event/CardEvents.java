package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardMoveCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.GainCardItem;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents implements CardUsePostCallback, CardDiscardCallback, CardMoveCallback {
    @Override
    public void cardUsePost(PlayerEntity user, ItemStack stack, @Nullable LivingEntity target) {
        if (user.getWorld() instanceof ServerWorld) {

            if (stack.getItem() == ModItems.WUXIE) stack.decrement(1); //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
            else if (!user.isCreative()) stack.decrement(1);

            //集智技能触发
            if (hasTrinket(SkillCards.JIZHI, user) && stack.isIn(Tags.Items.ARMOURY_CARD)) {
                GainCardItem.draw(user, 1);
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

        }
    }

    @Override
    public void cardDiscard(PlayerEntity player, ItemStack stack, int count, boolean fromEquip) {
        if (player.getWorld() instanceof ServerWorld) {


            //默认在最后移除被弃置的牌
            stack.decrement(count);
        }
    }

    @Override
    public void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, Type type) {
        if (from.getWorld() instanceof ServerWorld) {


            //如果是移动到物品栏的类型，则减少from的物品，给to等量的物品（移动到装备区有专门的方法）
            if (type == Type.INV_TO_INV || type == Type.EQUIP_TO_INV) {
                give(to, stack.copyWithCount(count));
                stack.decrement(count);
            }
        }
    }
}
