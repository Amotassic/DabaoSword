package com.amotassic.dabaosword.item.card;

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
            if (entity instanceof PlayerEntity player) {
                if (hasItem(player, ModItems.WUXIE)) {
                    voice(player, Sounds.WUXIE);
                    removeItem(player, ModItems.WUXIE);
                    jizhi(player); benxi(player);
                } else {give(user, stack1.copy()); stack1.setCount(0);}
            } else {give(user, stack1.copy()); stack1.setCount(0);}
            voice(user, Sounds.JIEDAO);
            if (!user.isCreative()) {stack.decrement(1);}
            jizhi(user); benxi(user);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
