package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SunshineSmile extends Item {
    public SunshineSmile(Properties settings) {super(settings);}

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (world instanceof ServerLevel sw && sw.getGameTime() % 1200 == 0) {
            var entry = ModTools.getEntry(ModItems.CRIT, entity);
            if (EnchantmentHelper.getItemEnchantmentLevel(entry, stack) == 0) {
                stack.enchant(entry, 1);
            }
        }
    }
}
