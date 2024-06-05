package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Objects;
import java.util.Random;

public class QingguoSkill extends SkillItem implements ModTools {
    public QingguoSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (cd == 0 && !stack1.isEmpty() && nonBasic(stack1)) {
                stack.set(ModItems.CD, 5);
                stack1.decrement(1);
                player.giveItemStack(ModItems.SHAN.getDefaultStack());
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QINGGUO1);} else {voice(player, Sounds.QINGGUO2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
