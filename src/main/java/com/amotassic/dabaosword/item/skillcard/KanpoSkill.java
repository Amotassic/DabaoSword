package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class KanpoSkill extends SkillItem {

    public KanpoSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = getCD(stack);
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.ARMOURY_CARD)) {
                setCD(stack, 10);
                viewAs(player, Tags.Items.ARMOURY_CARD, ModItems.WUXIE, Sounds.KANPO1, Sounds.KANPO2);
            }
        }
        super.tick(stack, slot, entity);
    }
}
