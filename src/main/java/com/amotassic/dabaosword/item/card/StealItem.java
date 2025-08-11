package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardMove;
import static com.amotassic.dabaosword.util.ModTools.*;

public class StealItem extends CardItem.Armoury {
    public StealItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && canSteal(entity)) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, target, Text.translatable("dabaosword.steal.title"), card, true, true, 1);
            } else {
                List<ItemStack> stacks = getItems(entity, isCard, true, false, true, false);
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    var exData = d().cards(chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardMove(entity, exData, player);
                }
            }
        }
    }

    @Override public boolean askForWuxie() {return true;}

    private boolean canSteal(LivingEntity entity) {
        int count = countAllCards(entity);
        for (ItemStack stack : getArmorItems(entity)) {count += stack.getCount();}
        return count > 0;
    }
}
