package com.amotassic.dabaosword.item.skillcard;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class ZhihengSkill extends SkillItem {
    public ZhihengSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        NbtCompound nbt = new NbtCompound();
        if (entity.getWorld() instanceof ServerWorld world) {
            if (stack.getNbt() == null) {nbt.putInt("zhi", 0); stack.setNbt(nbt);}
            else {
                int z = stack.getNbt().getInt("zhi");
                if (z < 10) {
                    if (world.getTime() % 100 == 0) {z++; nbt.putInt("zhi", z); stack.setNbt(nbt);}
                }
            }
        }
        super.tick(stack, slot, entity);
    }
}
