package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class DiscardItem extends CardItem.Armoury {
    public DiscardItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && countAllCards(entity) > 0) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, target, Text.translatable("dabaosword.discard.title", card.getName()), card, true, false, 1);
            } else {
                List<ItemStack> stacks = getItems(entity, isCard, true, false, true, false);
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    var exData = d().cards(chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardDiscard(entity, exData);
                }
            }
        } else {
            if (entity instanceof PlayerEntity player) { //如果是玩家则弃牌
                List<ItemStack> candidate = getItems(entity, isCard, true, false, true, true);
                if (!candidate.isEmpty()) {
                    ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                    player.sendMessage(Text.translatable("dabaosword.discard", user.getDisplayName(), player.getDisplayName(), chosen.toHoverableText()), false);
                    var exData = d().cards(chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardDiscard(player, exData);
                }
            } else { //如果不是玩家则随机弃置它的主副手物品和装备
                List<ItemStack> candidate = getItems(entity, s -> !s.isEmpty(), true, true, true, false);
                if (!candidate.isEmpty()) {
                    ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                    if (isCard(chosen)) {
                        var exData = d().cards(chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                        cardDiscard(entity, exData);
                    }
                    else chosen.decrement(1);
                }
            }
        }
    }

    @Override public boolean askForWuxie() {return true;}
}
