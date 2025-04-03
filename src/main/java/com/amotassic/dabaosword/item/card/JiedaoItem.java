package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.api.CardEvents.cardMove;
import static com.amotassic.dabaosword.util.ModTools.*;

public class JiedaoItem extends CardItem.Armoury {
    public JiedaoItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && !entity.getMainHandStack().isEmpty()) {
            onUse(user, user.getMainHandStack(), entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        ItemStack main = entity.getMainHandStack();
        if (user instanceof PlayerEntity player) {
            if (isCard(main)) {
                var exData = d().cards(main, 1);
                cardMove(entity, exData, player);
            } else {
                give(player, main.copy());
                main.setCount(0);
            }
        } else {
            user.setStackInHand(Hand.MAIN_HAND, main.copy());
            if (user instanceof MobEntity mob) mob.updateDropChances(EquipmentSlot.MAINHAND);
            main.setCount(0);
        }
    }

    @Override public boolean askForWuxie() {return true;}
}
