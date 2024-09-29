package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.cardUsePost;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class TaoyuanItem extends CardItem {
    public TaoyuanItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            ((ServerWorld) world).getPlayers().forEach(player -> player.heal(5.0F));
            ((ServerWorld) world).getPlayers().forEach(player -> voice(player, Sounds.TAOYUAN));
            cardUsePost(user, user.getStackInHand(hand), user);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
