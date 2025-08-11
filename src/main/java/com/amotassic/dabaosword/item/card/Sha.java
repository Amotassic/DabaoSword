package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Sha extends CardItem.Basic {
    public Sha(Settings settings) {super(settings);}

    @Override
    public void addTip(ItemStack stack, List<Text> tooltip) {
        tooltip.add(Text.translatable("item.dabaosword.sha.tip").formatted(Formatting.BOLD));

        if (stack.isOf(ModItems.SHA)) tooltip.add(getTip());
        if (stack.isOf(ModItems.FIRE_SHA)) tooltip.add(getTip(Formatting.RED));
        if (stack.isOf(ModItems.THUNDER_SHA)) tooltip.add(getTip(Formatting.BLUE));
    }

    public static void shaUse(LivingEntity user, ItemStack stack, Hand hand, float amount, LivingEntity... targets) {
        shaUse(user, stack, hand, amount, true, targets);
    }
    public static void shaUse(LivingEntity user, ItemStack stack, Hand hand, float amount, boolean consume, LivingEntity... targets) {
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
                if (!guanshi.isEmpty() && guanshi.getCD() == 0 && entity.hasStatusEffect(ModItems.INVULNERABLE)) {
                    guanshi.setCD(10); voice(user, guanshi.stack);
                    entity.removeStatusEffect(ModItems.INVULNERABLE);
                    if (sha.sha(user, entity, amount)) sha.effect(user, card.toStack(), entity);
                }
            }
        }
    }

    /**原本的伤害处理被取消，改为由杀造成伤害，因此一定要用{@link LivingEntity#damage(ServerWorld, DamageSource, float)}来造成伤害
     * @param amount 原本的伤害值*/
    public boolean sha(LivingEntity user, LivingEntity target, float amount) {
        return target.damage(world(user), ModDT.sha(user), amount + 5);
    }

    public static class Fire extends Sha {
        public Fire(Settings settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            target.setOnFire(true);
            return target.damage(world(user), ModDT.shaFire(user), amount);
        }

        @Override
        public void effect(LivingEntity user, ItemStack sha, LivingEntity target) {
            target.setOnFireFor(6);
        }
    }

    public static class Thunder extends Sha {
        public Thunder(Settings settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.damage(world(user), ModDT.shaThunder(user), amount + 5);
        }

        @Override
        public void effect(LivingEntity user, ItemStack sha, LivingEntity target) {
            ShandianEffect.summonLightning(target, true, false);
        }
    }
}