package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import java.util.Random;

public class QixiSkill extends SkillItem implements ModTools {
    public QixiSkill(Settings settings) {super(settings);}

    private final NbtCompound nbt = new NbtCompound();
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            if (cd == 0 && !stack1.isEmpty() && nonBasic(stack1)) {
                cd = 5; nbt.putInt("cooldown", cd); stack.setNbt(nbt);
                stack1.decrement(1);
                player.giveItemStack(ModItems.DISCARD.getDefaultStack());
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QIXI1);} else {voice(player, Sounds.QIXI2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
