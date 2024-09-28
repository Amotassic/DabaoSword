package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class TooHappyItem extends CardItem {
    public TooHappyItem(Settings settings) {super(settings);}

    //对生物使用后给予其10秒乐不思蜀效果
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof PlayerEntity player) {
                if (hasItem(player, ModItems.WUXIE)) {
                    cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                    voice(player, Sounds.WUXIE);
                } else player.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
            } else entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 15));
            cardUsePost(user, stack, entity);
            voice(user, Sounds.LEBU);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
