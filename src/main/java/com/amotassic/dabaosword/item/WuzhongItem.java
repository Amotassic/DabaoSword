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
                if (i < 0.33) {user.giveItemStack(new ItemStack(ModItems.SHAN));}
                else if (0.33 <= i && i < 0.4) {user.giveItemStack(new ItemStack(ModItems.PEACH));}
                else if (0.4 <= i && i < 0.5) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                else if (0.5 <= i && i < 0.6) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                else if (0.6 <= i && i < 0.7) {user.giveItemStack(new ItemStack(ModItems.STEAL));}
                else if (0.7 <= i && i < 0.8) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                else if (0.8 <= i && i < 0.9) {user.giveItemStack(new ItemStack(ModItems.WUZHONG));}
                else if (0.9 <= i && i < 0.91) {user.giveItemStack(new ItemStack(ModItems.ARROW_RAIN));}
                else if (0.91 <= i && i < 0.92) {user.giveItemStack(new ItemStack(ModItems.GUDINGDAO));}
                else {user.giveItemStack(new ItemStack(ModItems.DISCARD));}
            }
            if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.WUZHONG, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
        return super.use(world, user, hand);
    }
}
