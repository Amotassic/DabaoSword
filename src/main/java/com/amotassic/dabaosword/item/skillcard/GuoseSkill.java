package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class GuoseSkill extends SkillItem {
    public GuoseSkill(Settings settings) {super(settings);}

    private final NbtCompound nbt = new NbtCompound();
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            if (cd == 0 && !stack1.isEmpty() && stack1.getItem() == ModItems.SHAN) {
                cd = 15; nbt.putInt("cooldown", cd); stack.setNbt(nbt);
                stack1.decrement(1);
                player.giveItemStack(ModItems.TOO_HAPPY_ITEM.getDefaultStack());
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GUOSE1);} else {voice(player, Sounds.GUOSE2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
