package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class TooHappyItem extends CardItem.Armoury {
    public TooHappyItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide()) {
            onUse(user, user.getItemInHand(hand), hand, entity);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    //对生物使用后给予其乐不思蜀效果
    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        int duration = entity instanceof Player ? 5 : 15;
        entity.addEffect(new MobEffectInstance(ModItems.TOO_HAPPY, 20 * duration));
    }

    @Override public boolean rangedUse() {return true;}

    @Override public boolean askForWuxie() {return true;}
}
