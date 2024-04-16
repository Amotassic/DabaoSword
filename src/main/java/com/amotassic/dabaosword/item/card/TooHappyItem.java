package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TooHappyItem extends CardItem implements ModTools {
    public TooHappyItem(Settings settings) {
        super(settings);
    }

    //攻击命中敌人给予其10秒乐不思蜀效果
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            if (target instanceof PlayerEntity player1) {
                if (hasItem(player1, ModItems.WUXIE)) {
                    removeItem(player1, ModItems.WUXIE);
                    voice(player1, Sounds.WUXIE);
                } else {
                    target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                }
            } else {
                target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 15));
            }
            if (!player.isCreative()) {stack.decrement(1);}
            voice(player, Sounds.LEBU);
        }
        return super.postHit(stack, target, attacker);
    }
}
