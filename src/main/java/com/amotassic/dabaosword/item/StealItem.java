package com.amotassic.dabaosword.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class StealItem extends Item {
    public StealItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.steal.tooltip1"));
        tooltip.add(Text.translatable("item.dabaosword.steal.tooltip2"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target) {
            DefaultedList<ItemStack> inventory = target.getInventory().main;
            List<Integer> nonEmptySlots = IntStream.range(0, inventory.size()).filter(i -> !inventory.get(i).isEmpty()).boxed().toList();
            if (!nonEmptySlots.isEmpty()) {
                int slot = nonEmptySlots.get(new Random().nextInt(nonEmptySlots.size()));
                ItemStack item = inventory.get(slot);
                user.giveItemStack(item.copy());
                item.setCount(0);
                if (!user.isCreative()) {stack.decrement(1);}
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
