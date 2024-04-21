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

public class JuedouItem extends CardItem implements ModTools {
    public JuedouItem(Settings settings) {
        super(settings);
    }

    //给与双方决斗效果
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            if (entity instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                removeItem(player, ModItems.WUXIE);
                jizhi(player);
                voice(player, Sounds.WUXIE);
            } else {
                user.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 10));
                entity.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 10));
            }
            if (!user.isCreative()) {stack.decrement(1);}
            jizhi(user);
            voice(user, Sounds.JUEDOU);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
