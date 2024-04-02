package com.amotassic.dabaosword.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class CardItem extends Item {
    public CardItem(Settings settings) {super(settings);}
    //这是一个空类，用于判断物品是否是卡牌
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        if (stack.getItem() == ModItems.WUXIE) {
            tooltip.add(Text.translatable("item.dabaosword.wuxie.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.wuxie.tooltip2"));
        }
    }
}
