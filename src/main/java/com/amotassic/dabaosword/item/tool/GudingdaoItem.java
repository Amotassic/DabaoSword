package com.amotassic.dabaosword.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class GudingdaoItem extends Item {
    public GudingdaoItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> tooltip, @NonNull TooltipFlag type) {
        tooltip.accept(Component.translatable("item.dabaosword.gudingdao.tooltip").withStyle(ChatFormatting.GREEN));
        tooltip.accept(Component.translatable("item.dabaosword.gudingdao.tooltip1").withStyle(ChatFormatting.AQUA));
        tooltip.accept(Component.literal(""));
    }
}