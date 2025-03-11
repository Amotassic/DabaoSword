package com.amotassic.dabaosword.item.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GudingdaoItem extends SwordItem {
    public GudingdaoItem(Settings settings) {
        super(ToolMaterial.NETHERITE, 5, -2.4f, settings);
    }

    @Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type){
        tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
        tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip1").formatted(Formatting.AQUA));
        tooltip.add(Text.literal(""));
	}
}