package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Objects;

public class HuojiSkill extends SkillItem implements ModTools {

    public HuojiSkill(Settings settings) {super(settings);}

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.BASIC_CARD)) {
                stack.set(ModItems.CD, 15);
                viewAs(player, Tags.Items.BASIC_CARD, ModItems.FIRE_ATTACK, Sounds.HUOJI1, Sounds.HUOJI2);
            }
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; stack.set(ModItems.CD, cd);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
