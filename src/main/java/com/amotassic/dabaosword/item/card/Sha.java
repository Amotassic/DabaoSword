package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Sha extends CardItem.Basic {
    public Sha(Properties settings) {super(settings);}

    @Override
    public void addTip(ItemStack stack, List<Component> tooltip) {
        tooltip.add(Component.translatable("item.dabaosword.sha.tip").withStyle(ChatFormatting.BOLD));

        if (stack.is(ModItems.SHA)) tooltip.add(getTip());
        if (stack.is(ModItems.FIRE_SHA)) tooltip.add(getTip(ChatFormatting.RED));
        if (stack.is(ModItems.THUNDER_SHA)) tooltip.add(getTip(ChatFormatting.BLUE));
    }

    public static void shaUse(LivingEntity user, ItemStack stack, InteractionHand hand, float amount, LivingEntity... targets) {
        shaUse(user, stack, hand, amount, true, targets);
    }
    public static void shaUse(LivingEntity user, ItemStack stack, InteractionHand hand, float amount, boolean consume, LivingEntity... targets) {
        var card = c(stack); var cardData = d().cards(card, card.count);
        if (consume) CardEvents.cardUseAndDecrement(user, stack, hand);
        voice(user, card.item());
        List<LivingEntity> owners = getSkillOwners(user);
        //触发卡牌使用事件
        owners.forEach(player -> getResult(Trigger.LOSE_CARD_USE, player, user, cardData));

        var data = cardData.withTargets(targets);
        //触发修改卡牌目标的技能
        owners.forEach(player -> getResult(Trigger.ADD_TARGET, player, user, data));
        owners.forEach(player -> getResult(Trigger.DROP_TARGET, player, user, data));
        for (LivingEntity entity : data.targets) {
            //当卡牌指定目标后，触发使用者的技能
            getResult(Trigger.SELECT_TARGET, user, entity, cardData);
            //当有玩家成为卡牌目标后，触发玩家的技能
            owners.forEach(player -> getResult(Trigger.BECOME_TARGET, player, entity, cardData));

            Sha sha = (Sha) card.toStack().getItem();
            if (sha.sha(user, entity, amount)) sha.effect(user, card.toStack(), entity);
            else { //如果杀被无效化了，就会尝试触发贯石斧的效果
                var guanshi = s(trinketItem(ModItems.GUANSHI, user));
                if (!guanshi.isEmpty() && guanshi.getCD() == 0 && entity.hasEffect(ModItems.INVULNERABLE)) {
                    guanshi.setCD(10); voice(user, guanshi.stack);
                    entity.removeEffect(ModItems.INVULNERABLE);
                    if (sha.sha(user, entity, amount)) sha.effect(user, card.toStack(), entity);
                }
            }
        }
    }

    /**原本的伤害处理被取消，改为由杀造成伤害，因此一定要用{@link LivingEntity#hurtServer(ServerLevel, DamageSource, float)}来造成伤害
     * @param amount 原本的伤害值*/
    public boolean sha(LivingEntity user, LivingEntity target, float amount) {
        return target.hurtServer(world(user), ModDT.sha(user), amount + 5);
    }

    public static class Fire extends Sha {
        public Fire(Properties settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.hurtServer(world(user), ModDT.shaFire(user), amount);
        }

        @Override
        public void effect(LivingEntity user, ItemStack sha, LivingEntity target) {
            target.setRemainingFireTicks(120);
        }
    }

    public static class Thunder extends Sha {
        public Thunder(Properties settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.hurtServer(world(user), ModDT.shaThunder(user), amount + 5);
        }

        @Override
        public void effect(LivingEntity user, ItemStack sha, LivingEntity target) {
            ShandianEffect.summonLightning(target, true, false);
        }
    }
}