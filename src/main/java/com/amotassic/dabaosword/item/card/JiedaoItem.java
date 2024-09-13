package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardMoveCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JiedaoItem extends CardItem {
    public JiedaoItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack stack1 = entity.getMainHandStack();
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && !stack1.isEmpty()) {
            if (entity instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                CardUsePostCallback.EVENT.invoker().cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                voice(player, Sounds.WUXIE);
            } else {
                if (isCard(stack1)) CardMoveCallback.EVENT.invoker().cardMove(entity, user, stack1, stack1.getCount(), CardMoveCallback.Type.INV_TO_INV);
                else give(user, stack1.copy()); stack1.setCount(0);
            }
            voice(user, Sounds.JIEDAO);
            CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, entity);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
