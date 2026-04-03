package com.amotassic.dabaosword.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.api.CardEvents.cardMove;
import static com.amotassic.dabaosword.util.ModTools.*;

public class JiedaoItem extends CardItem.Armoury {
    public JiedaoItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && !entity.getMainHandItem().isEmpty()) {
            onUse(user, user.getItemInHand(hand), hand, entity);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        ItemStack main = entity.getMainHandItem();
        if (user instanceof Player player) {
            if (isCard(main)) {
                var exData = d().cards(main, main.getCount());
                cardMove(entity, exData, player);
            } else {
                give(player, main.copy());
                main.setCount(0);
            }
        } else {
            user.setItemInHand(InteractionHand.MAIN_HAND, main.copy());
            if (user instanceof Mob mob) mob.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            main.setCount(0);
        }
    }

    @Override public boolean askForWuxie() {return true;}
}
