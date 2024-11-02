package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardEvents implements CardCBs.PostUse, CardCBs.Discard, CardCBs.Move, CardCBs.PreUse {
    @Override
    public boolean cardUsePre(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        if (target != null) {
            if (isBlackCard.test(stack) && stack.isIn(Tags.Items.ARMOURY_CARD) && hasTrinket(SkillCards.WEIMU, target)) {
                voice(target, Sounds.WEIMU);
                ModTools.cardUsePost(user, stack, target);
                return false;
            }

            if (stack.isIn(Tags.Items.TRIGGER_WUXIE) && hasCard(target, s -> s.isOf(ModItems.WUXIE))) {
                ModTools.cardUsePre(target, new ItemStack(ModItems.WUXIE), null); //递归触发无懈，因此不用再写消耗和执行效果
                ModTools.cardUsePost(user, stack, target);
                return false;
            }
        }
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
    public void cardDiscard(LivingEntity entity, ItemStack stack, int count, boolean fromEquip) {
        if (XingshangTrigger(entity, stack)) return;

        //弃置牌后，玩家的死亡判断是有必要的
        if (entity instanceof PlayerEntity player && player.isAlive()) {
            if (hasTrinket(SkillCards.LIANYING, player) && !fromEquip && countCards(player) == 0) lianyingTrigger(player);

            if (hasTrinket(SkillCards.XIAOJI, player) && fromEquip) xiaojiTrigger(player);
        }
    }

    @Override
    public void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, CardCBs.T type) {
        if (type == CardCBs.T.INV_TO_EQUIP || type == CardCBs.T.INV_TO_INV) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.LIANYING, player) && countCards(player) == 0) lianyingTrigger(player);
        }

        if (type == CardCBs.T.EQUIP_TO_INV || type == CardCBs.T.EQUIP_TO_EQUIP) {
            if (from instanceof PlayerEntity player && hasTrinket(SkillCards.XIAOJI, player)) xiaojiTrigger(player);
        }
    }

    private static boolean XingshangTrigger(LivingEntity entity, ItemStack stack) {
        if (entity.isAlive()) return false;
        for (PlayerEntity player : entity.getWorld().getPlayers()) {
            if (hasTrinket(SkillCards.XINGSHANG, player) && player.distanceTo(entity) <= 25 && player != entity) {
                if (!player.getCommandTags().contains("xingshang")) voice(player, Sounds.XINGSHANG);
                player.addCommandTag("xingshang"); //防止同时触发大量语音播放
                give(player, stack.copy());
                return true;
            }
        }
        return false;
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
