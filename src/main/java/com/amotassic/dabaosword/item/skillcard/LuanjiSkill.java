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
import java.util.Random;

public class LuanjiSkill extends SkillItem implements ModTools {

    public LuanjiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.CARD) && stack1.getCount() > 1) {
                stack.set(ModItems.CD, 15);
                stack1.decrement(2);
                player.giveItemStack(ModItems.WANJIAN.getDefaultStack());
                float i = new Random().nextFloat();
                if (i < 0.25) {voice(player, Sounds.LUANJI1);} else if (0.25 <= i && i < 0.5) {voice(player, Sounds.LUANJI2);}
                else if (0.5 <= i && i < 0.75) {voice(player, Sounds.LUANJI3);} else {voice(player, Sounds.LUANJI4);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
