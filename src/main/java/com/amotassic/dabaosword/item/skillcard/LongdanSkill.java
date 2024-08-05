package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class LongdanSkill extends SkillItem {
    public LongdanSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getOffHandStack();
            if (world.getTime() % 20 == 0 && stack1.isIn(Tags.Items.BASIC_CARD)) {
                stack1.decrement(1);
                if (stack1.isIn(Tags.Items.SHA)) give(player, new ItemStack(ModItems.SHAN));
                if (stack1.getItem() == ModItems.SHAN) give(player, new ItemStack(ModItems.SHA));
                if (stack1.getItem() == ModItems.PEACH) give(player, new ItemStack(ModItems.JIU));
                if (stack1.getItem() == ModItems.JIU) give(player, new ItemStack(ModItems.PEACH));
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.LONGDAN1);} else {voice(player, Sounds.LONGDAN2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
