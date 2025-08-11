package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class BingliangItem extends CardItem.Armoury {
    public BingliangItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    //对生物使用后给予其兵粮寸断效果
    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1, 1));
    }

    @Override public boolean askForWuxie() {return true;}
}
