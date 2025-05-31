package com.amotassic.dabaosword.item.tool;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class GudingdaoItem extends Item {
    public GudingdaoItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
        tooltip.accept(Text.translatable("item.dabaosword.gudingdao.tooltip1").formatted(Formatting.AQUA));
        tooltip.accept(Text.literal(""));
    }
}