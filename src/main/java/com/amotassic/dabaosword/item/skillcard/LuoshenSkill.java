package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class LuoshenSkill extends ActiveSkill implements ModTools {
    public LuoshenSkill(Settings settings) {super(settings);}

    private int tick = 0;
    private final NbtCompound nbt = new NbtCompound();
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
