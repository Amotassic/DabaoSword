package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static com.amotassic.dabaosword.util.ModTools.*;

public class GuoseSkill extends SkillItem {
    public GuoseSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getOffHandStack();
            int cd = getCD(stack);
            if (cd == 0 && !stack1.isEmpty() && stack1.getItem() == ModItems.SHAN) {
                setCD(stack, 15);
                stack1.decrement(1);
                give(player, ModItems.TOO_HAPPY_ITEM.getDefaultStack());
                voice(player, Sounds.GUOSE);
            }
        }
        super.tick(stack, slot, entity);
    }
}
