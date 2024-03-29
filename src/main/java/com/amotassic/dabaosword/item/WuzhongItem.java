package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class WuzhongItem extends CardItem {
    public WuzhongItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip1"));
        tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip2"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            for (int n = 0; n<2; n++) {
                float i = new Random().nextFloat();
                if (i < 0.25) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                else if (0.25 <= i && i < 0.5) {user.giveItemStack(new ItemStack(ModItems.PEACH));}
                else if (0.5 <= i && i < 0.75) {user.giveItemStack(new ItemStack(ModItems.STEAL));}
                else {user.giveItemStack(new ItemStack(ModItems.DISCARD));}
            }
            if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.WUZHONG, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
        return super.use(world, user, hand);
    }
}
