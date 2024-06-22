package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.ModItems;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class FangtianWeapon extends EquipmentItem {
    public FangtianWeapon(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            if (stack.get(ModItems.CD) == null) {stack.set(ModItems.CD, 0);stack.set(ModItems.TAGS, 0);
            } else {
                int cd = Objects.requireNonNull(stack.get(ModItems.CD));
                int time = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (cd > 0) cd--; stack.set(ModItems.CD, cd);
                if (time > 0) time--; stack.set(ModItems.TAGS, time);
            }
        }
    }
}
