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

public class DiscardItem extends CardItem {
    public DiscardItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && countAllCards(entity) > 0) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, false, true, false, 1);
            } else {
                List<ItemStack> stacks = getItems(entity, isCard, true, false, true, false);
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    cardDiscard(entity, chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardUsePost(player, stack, entity);
                }
            }
        } else {
            if (entity instanceof PlayerEntity player) { //如果是玩家则弃牌
                List<ItemStack> candidate = getItems(entity, isCard, true, false, true, true);
                if (!candidate.isEmpty()) {
                    ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                    player.sendMessage(Text.translatable("dabaosword.discard", user.getDisplayName(), player.getDisplayName(), chosen.toHoverableText()), false);
                    cardDiscard(player, chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    cardUsePost(user, stack, entity);
                }
            } else { //如果不是玩家则随机弃置它的主副手物品和装备
                var candidate = getItems(entity, s -> !s.isEmpty(), true, true, true, false);
                if (!candidate.isEmpty()) {
                    ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                    if (isCard(chosen)) cardDiscard(entity, chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                    else chosen.decrement(1);
                    cardUsePost(user, stack, entity);
                }
            }
        }
    }

    @Override
    public boolean notImmediatelyEffective() {return true;}
}
