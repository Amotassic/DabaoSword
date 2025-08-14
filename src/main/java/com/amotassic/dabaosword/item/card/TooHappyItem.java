package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class TooHappyItem extends CardItem.Armoury {
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    //对生物使用后给予其乐不思蜀效果
    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        int duration = entity instanceof PlayerEntity ? 5 : 15;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * duration));
    }

    @Override public boolean askForWuxie() {return true;}
}
