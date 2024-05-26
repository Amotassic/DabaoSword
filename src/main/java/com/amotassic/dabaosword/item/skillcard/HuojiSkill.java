package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class HuojiSkill extends SkillItem implements ModTools {
    public HuojiSkill(Settings settings) {super(settings);}

    private int tick = 0;
    private final NbtCompound nbt = new NbtCompound();
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.BASIC_CARD)) {
                cd = 15; nbt.putInt("cooldown", cd); stack.setNbt(nbt);
                viewAs(player, Tags.Items.BASIC_CARD, ModItems.FIRE_ATTACK, Sounds.HUOJI1, Sounds.HUOJI2);
            }
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
