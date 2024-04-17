package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class JuedouItem extends CardItem implements ModTools {
    public JuedouItem(Settings settings) {
        super(settings);
    }

    //给与攻击和受击双方决斗效果
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            if (target instanceof PlayerEntity player1 && hasItem(player1, ModItems.WUXIE)) {
                removeItem(player1, ModItems.WUXIE);
                jizhi(player1);
                voice(player1, Sounds.WUXIE);
            } else {
                attacker.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 15));
                target.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 15));
            }
            if (!player.isCreative()) {stack.decrement(1);}
            jizhi(player);
            voice(player, Sounds.JUEDOU);
        }
        return super.postHit(stack, target, attacker);
    }
}
