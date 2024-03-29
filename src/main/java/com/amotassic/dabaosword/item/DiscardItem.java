package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class DiscardItem extends CardItem {
    public DiscardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.discard.tooltip1"));
        tooltip.add(Text.translatable("item.dabaosword.discard.tooltip2"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target) {
            DefaultedList<ItemStack> inventory = target.getInventory().main;
            List<Integer> nonEmptySlots = IntStream.range(0, inventory.size()).filter(i -> !inventory.get(i).isEmpty()).boxed().toList();
            if (!nonEmptySlots.isEmpty()) {
                int slot = nonEmptySlots.get(new Random().nextInt(nonEmptySlots.size()));
                ItemStack item = inventory.get(slot);
                item.setCount(0);
                if (!user.isCreative()) {stack.decrement(1);}
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.GUOHE, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
