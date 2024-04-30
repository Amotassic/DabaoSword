package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class JiuItem extends CardItem implements ModTools {
    public JiuItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.hasStatusEffect(StatusEffects.STRENGTH) && !world.isClient && hand == Hand.MAIN_HAND) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 10, 1));
            voice(user, Sounds.JIU); benxi(user);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
