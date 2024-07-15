package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class ZhihengSkill extends SkillItem {
    public ZhihengSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (stack.get(ModItems.TAGS) == null) stack.set(ModItems.TAGS, 0);
            else {
                int z = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (z < 10) {
                    if (world.getTime() % 100 == 0) {z++; stack.set(ModItems.TAGS, z);}
                }
            }
        }
        super.tick(stack, slot, entity);
    }
}
