package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class BingliangItem extends CardItem implements ModTools {
    public BingliangItem(Settings settings) {
        super(settings);
    }

    //对生物使用后给予其兵粮寸断效果
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                removeItem(player, ModItems.WUXIE);
                jizhi(player);
                voice(player, Sounds.WUXIE);
            } else {
                entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
            }
            if (!user.isCreative()) {stack.decrement(1);}
            jizhi(user);
            voice(user, Sounds.BINGLIANG);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
