package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class StealItem extends CardItem {
    public StealItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, Text.translatable("dabaosword.steal.title"), stack, targetInv(target, true, true, 1));
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                if (isCard(entity.getMainHandStack())) stacks.add(entity.getMainHandStack());
                if (isCard(entity.getOffHandStack())) stacks.add(entity.getOffHandStack());
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    voice(user, Sounds.SHUNSHOU);
                    cardMove(entity, player, chosen, 1, CardCBs.T.INV_TO_INV);
                    nonPreUseCardDecrement(player, stack, entity);
                }
            }
        } else nonPreUseCardDecrement(user, stack, entity);
    }
}
