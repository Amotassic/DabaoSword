package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class BingliangItem extends CardItem.Armoury {
    public BingliangItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity target, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide()) {
            onUse(user, user.getItemInHand(hand), hand, target);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    //对生物使用后给予其兵粮寸断效果
    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModItems.BINGLIANG, -1, 1));
    }

    @Override public boolean askForWuxie() {return true;}
}
