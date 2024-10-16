package com.amotassic.dabaosword.item.equipment;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

public class GudingdaoItem extends SwordItem {
    public GudingdaoItem(Settings Settings) {
        super(ToolMaterials.NETHERITE, 5, -2.4F,new FabricItemSettings().maxDamage(999).rarity(Rarity.EPIC));
    }
    
    @Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
        tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip1").formatted(Formatting.AQUA));
        tooltip.add(Text.literal(""));
	}
}