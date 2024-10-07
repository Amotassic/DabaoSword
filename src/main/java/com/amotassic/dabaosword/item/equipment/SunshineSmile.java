package com.amotassic.dabaosword.item.equipment;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SunshineSmile extends Item implements Equipment {
    public SunshineSmile(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.sunshine_smile.tooltip"));
    }

    @Override
    public EquipmentSlot getSlotType() {return EquipmentSlot.HEAD;}
}
