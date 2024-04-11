package com.amotassic.dabaosword.items.card;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class HoushaolianyingItem extends Item {
    public HoushaolianyingItem(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.huoshaolianying.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient){
            getNearbyEntity(user);
        }
        return super.use(world, user, hand);
    }

    private static void getNearbyEntity(PlayerEntity player){
        Box box = new Box(player.getBlockPos().add(-10,-10,-10),player.getBlockPos().add(10,10,10));
        player.getWorld().getOtherEntities(player,box,entity -> true).forEach(entity -> {
            if (entity != player){
                entity.setFireTicks(100);
            }
        });
    }
}
