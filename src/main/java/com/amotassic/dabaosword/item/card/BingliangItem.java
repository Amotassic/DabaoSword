package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class BingliangItem extends CardItem implements ModTools {
    public BingliangItem(Settings settings) {
        super(settings);
    }

    //攻击命中敌人给予其兵粮寸断效果
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            if (target instanceof PlayerEntity player1 && hasItem(player1, ModItems.WUXIE)) {
                removeItem(player1, ModItems.WUXIE);
                jizhi(player1);
                voice(player1, Sounds.WUXIE);
            } else {
                target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
            }
            if (!player.isCreative()) {stack.decrement(1);}
            jizhi(player);
            voice(player, Sounds.BINGLIANG);
        }
        return super.postHit(stack, target, attacker);
    }
}
