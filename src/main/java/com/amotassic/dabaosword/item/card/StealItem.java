package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.event.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;

public class StealItem extends CardItem {
    public StealItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && canSteal(entity)) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, Text.translatable("dabaosword.steal.title"), stack, false, true, true, 1);
            } else {
                List<ItemStack> stacks = getItems(entity, isCard, true, false, true, false);
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    cardMove(entity, player, chosen, 1, isEquipped(entity, s -> s.equals(chosen)), false);
                    cardUsePost(player, stack, entity);
                }
            }
        } else cardUsePost(user, stack, entity);
    }

    @Override
    public boolean notImmediatelyEffective() {return true;}

    private boolean canSteal(LivingEntity entity) {
        int count = countAllCards(entity);
        for (ItemStack stack : entity.getArmorItems()) {count += stack.getCount();}
        return count > 0;
    }
}
