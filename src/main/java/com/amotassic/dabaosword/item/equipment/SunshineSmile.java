package com.amotassic.dabaosword.item.equipment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.List;

public class SunshineSmile extends Item implements Equipment {
    public SunshineSmile() {super(new Item.Settings().maxDamage(999).rarity(Rarity.UNCOMMON));}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.sunshine_smile.tooltip"));
    }

    @Override
    public EquipmentSlot getSlotType() {return EquipmentSlot.HEAD;}
}
