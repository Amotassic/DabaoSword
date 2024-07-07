package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class KanpoSkill extends SkillItem {
    public KanpoSkill(Settings settings) {super(settings);}

    private final NbtCompound nbt = new NbtCompound();
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            if (cd == 0 && !stack1.isEmpty() && nonBasic(stack1)) {
                cd = 10; nbt.putInt("cooldown", cd); stack.setNbt(nbt);
                viewAs(player, Tags.Items.ARMOURY_CARD, ModItems.WUXIE, Sounds.KANPO1, Sounds.KANPO2);
            }
        }
        super.tick(stack, slot, entity);
    }
}
