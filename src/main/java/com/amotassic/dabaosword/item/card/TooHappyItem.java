package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.cardUsePre;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class TooHappyItem extends CardItem {
    public TooHappyItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    //对生物使用后给予其乐不思蜀效果
    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        int duration = entity instanceof PlayerEntity ? 5 : 15;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * duration));
        voice(user, Sounds.LEBU);
        super.cardUse(user, stack, entity);
    }
}
