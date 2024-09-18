package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class WanjianItem extends CardItem {

    public WanjianItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            user.addCommandTag("wanjian");
            user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 15,1,false,false,false));
            CardUsePostCallback.EVENT.invoker().cardUsePost(user, user.getStackInHand(hand), null);
            voice(user, Sounds.WANJIAN);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
