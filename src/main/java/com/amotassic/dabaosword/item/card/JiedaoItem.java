package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.api.event.CardEvents.cardMove;
import static com.amotassic.dabaosword.api.event.CardEvents.cardUsePre;
import static com.amotassic.dabaosword.util.ModTools.give;
import static com.amotassic.dabaosword.util.ModTools.isCard;

public class JiedaoItem extends CardItem {
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && !entity.getMainHandStack().isEmpty()) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        ItemStack stack1 = entity.getMainHandStack();
        if (user instanceof PlayerEntity player) {
            if (isCard(stack1)) cardMove(entity, player, stack1, stack1.getCount(), false, false);
            else {
                give(player, stack1.copy());
                stack1.setCount(0);
            }
        } else {
            user.setStackInHand(Hand.MAIN_HAND, stack1.copy());
            if (user instanceof MobEntity mob) mob.updateDropChances(EquipmentSlot.MAINHAND);
            stack1.setCount(0);
        }
    }
}
