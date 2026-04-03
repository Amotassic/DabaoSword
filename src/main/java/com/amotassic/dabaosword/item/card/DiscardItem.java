package com.amotassic.dabaosword.item.card;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class DiscardItem extends CardItem.Armoury {
    public DiscardItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && countAllCards(entity) > 0) {
            onUse(user, user.getItemInHand(hand), hand, entity);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        if (user instanceof Player player) {
            if (entity instanceof Player target) {
                openInv(player, target, target, Component.translatable("dabaosword.discard.title", card.getDisplayName()), card, true, false, 1);
            } else {
                List<ItemStack> stacks = getItems(entity, isCard, true, false, true, false);
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    var exData = d().cards(chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardDiscard(entity, exData);
                }
            }
        } else {
            if (entity instanceof Player player) { //如果是玩家则弃牌
                List<ItemStack> candidate = getItems(entity, isCard, true, false, true, true);
                if (!candidate.isEmpty()) {
                    ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                    player.sendSystemMessage(Component.translatable("dabaosword.discard", user.getDisplayName(), player.getDisplayName(), chosen.getDisplayName()));
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
                    else chosen.shrink(1);
                }
            }
        }
    }

    @Override public boolean rangedUse() {return true;}

    @Override public boolean askForWuxie() {return true;}
}
