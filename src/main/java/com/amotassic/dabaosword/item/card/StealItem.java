package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.network.ServerNetworking.openInv;
import static com.amotassic.dabaosword.network.ServerNetworking.targetInv;
import static com.amotassic.dabaosword.util.ModTools.*;

public class StealItem extends CardItem {
    public StealItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target && !user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (hasItem(target, ModItems.WUXIE)) {
                voice(target, Sounds.WUXIE);
                voice(user, Sounds.SHUNSHOU);
                if (!user.isCreative()) {stack.decrement(1);}
                jizhi(user); benxi(user);
                removeItem(target, ModItems.WUXIE);
                jizhi(target); benxi(target);
            } else {
                openInv(user, target, Text.translatable("dabaosword.steal.title"), targetInv(target, true, true, 1, user.getMainHandStack()));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
