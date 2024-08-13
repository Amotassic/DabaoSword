package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class QixiSkill extends SkillItem {
    public QixiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getOffHandStack();
            int cd = getCD(stack);
            if (cd == 0 && !stack1.isEmpty() && nonBasic(stack1)) {
                setCD(stack, 5);
                stack1.decrement(1);
                give(player, ModItems.DISCARD.getDefaultStack());
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QIXI1);} else {voice(player, Sounds.QIXI2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
