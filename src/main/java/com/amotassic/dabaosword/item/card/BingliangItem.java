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

public class BingliangItem extends CardItem {
    public BingliangItem(Settings settings) {super(settings);}

    //对生物使用后给予其兵粮寸断效果
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof PlayerEntity player && hasWuxie(player)) {
                cardUsePost(player, getWuxie(player), null);
                voice(player, Sounds.WUXIE);
            } else entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
            cardUsePost(user, stack, entity);
            voice(user, Sounds.BINGLIANG);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
