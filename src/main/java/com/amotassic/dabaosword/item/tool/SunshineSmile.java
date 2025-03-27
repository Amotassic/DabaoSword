package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class SunshineSmile extends Item {
    public SunshineSmile(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.sunshine_smile.tooltip"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld sw && sw.getTime() % 1200 == 0) {
            var entry = ModTools.getEntry(ModItems.CRIT);
            if (EnchantmentHelper.getLevel(entry, stack) == 0) {
                stack.addEnchantment(entry, 1);
            }
        }
    }
}
