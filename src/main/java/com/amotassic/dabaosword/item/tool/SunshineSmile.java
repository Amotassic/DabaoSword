package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class SunshineSmile extends Item {
    public SunshineSmile(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world instanceof ServerWorld sw && sw.getTime() % 1200 == 0) {
            var entry = ModTools.getEntry(ModItems.CRIT, entity);
            if (EnchantmentHelper.getLevel(entry, stack) == 0) {
                stack.addEnchantment(entry, 1);
            }
        }
    }
}
