package com.amotassic.dabaosword.item.equipment;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class FangtianWeapon extends EquipmentItem {
    public FangtianWeapon(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            NbtCompound nbt = new NbtCompound();
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cd");
            if (cd > 0 && world.getTime() % 20 == 0) cd--; nbt.putInt("cd", cd); stack.setNbt(nbt);
        }
        super.tick(stack, slot, entity);
    }
}
