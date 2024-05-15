package com.amotassic.dabaosword.item.equipment;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class FangtianWeapon extends EquipmentItem {
    public FangtianWeapon(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() == null) {nbt.putInt("cd", 0); nbt.putInt("time", 0); stack.setNbt(nbt);
            } else {
                int cd = stack.getNbt().getInt("cd");
                int time = stack.getNbt().getInt("time");
                if (cd > 0) cd--; nbt.putInt("cd", cd); stack.setNbt(nbt);
                if (time > 0) time--; nbt.putInt("time", time); stack.setNbt(nbt);
            }
        }
    }
}
