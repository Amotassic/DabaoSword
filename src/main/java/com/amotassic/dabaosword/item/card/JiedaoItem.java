package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JiedaoItem extends CardItem {
    public JiedaoItem(Settings settings) {super(settings);}

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
            if (isCard(stack1)) cardMove(entity, player, stack1, stack1.getCount(), CardCBs.T.INV_TO_INV);
            else {
                give(player, stack1.copy());
                stack1.setCount(0);
            }
        } else {
            user.setStackInHand(Hand.MAIN_HAND, stack1.copy());
            stack1.setCount(0);
        }
        voice(user, Sounds.JIEDAO);
        super.cardUse(user, stack, entity);
    }
}
