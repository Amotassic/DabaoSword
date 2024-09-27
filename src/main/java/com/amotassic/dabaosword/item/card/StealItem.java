package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardMoveCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
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
            if (entity instanceof PlayerEntity target) {
                if (hasItem(target, ModItems.WUXIE)) {
                    voice(target, Sounds.WUXIE);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(target, getItem(target, ModItems.WUXIE), null);
                    voice(user, Sounds.SHUNSHOU);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, entity);
                } else {
                    openInv(user, target, Text.translatable("dabaosword.steal.title"), stack, targetInv(target, true, true, 1));
                }
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                if (isCard(entity.getMainHandStack())) stacks.add(entity.getMainHandStack());
                if (isCard(entity.getOffHandStack())) stacks.add(entity.getOffHandStack());
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    voice(user, Sounds.SHUNSHOU);
                    CardMoveCallback.EVENT.invoker().cardMove(entity, user, chosen, 1, CardMoveCallback.Type.INV_TO_INV);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, entity);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
