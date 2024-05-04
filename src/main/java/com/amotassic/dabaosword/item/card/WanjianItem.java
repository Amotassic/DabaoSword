package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WanjianItem extends CardItem implements ModTools {

    public WanjianItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 15,1,false,false,false));
            voice(user, Sounds.WANJIAN);
            jizhi(user); benxi(user);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
