package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.Sounds;
import com.amotassic.dabaosword.item.ModItems;
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

public class GainCardItem extends CardItem {
    public GainCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        if (stack.getItem() == ModItems.WUZHONG) {
            tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip2"));
        } else {tooltip.add(Text.translatable("item.dabaosword.gain_card.tooltip"));}
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && hand == Hand.MAIN_HAND) {
            int m;
            //牌堆，潜行时直接摸64张
            if (user.getStackInHand(hand).getItem() == ModItems.CARD_PILE) {
                if (user.isSneaking()) {m=64;} else {m=1;}
                for (int n = 0; n<m; n++) {
                    float i = new Random().nextFloat();
                    if (i < 0.29) {user.giveItemStack(new ItemStack(ModItems.SHAN));}
                    else if (0.29 <= i && i < 0.41) {user.giveItemStack(new ItemStack(ModItems.PEACH));}
                    else if (0.41 <= i && i < 0.5) {user.giveItemStack(new ItemStack(ModItems.JIU));}
                    else if (0.5 <= i && i < 0.51) {user.giveItemStack(new ItemStack(ModItems.GUDINGDAO));}
                    else if (0.51 <= i && i < 0.52) {user.giveItemStack(new ItemStack(ModItems.ARROW_RAIN));}
                    else if (0.52 <= i && i < 0.53) {user.giveItemStack(new ItemStack(ModItems.RATTAN_CHESTPLATE));}
                    else if (0.53 <= i && i < 0.54) {user.giveItemStack(new ItemStack(ModItems.RATTAN_LEGGINGS));}
                    else if (0.54 <= i && i < 0.55) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else if (0.55 <= i && i < 0.56) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.56 <= i && i < 0.57) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.57 <= i && i < 0.58) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.59 <= i && i < 0.64) {user.giveItemStack(new ItemStack(ModItems.WUZHONG));}
                    else if (0.64 <= i && i < 0.69) {user.giveItemStack(new ItemStack(ModItems.TIESUO));}
                    else if (0.74 <= i && i < 0.78) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.78 <= i && i < 0.82) {user.giveItemStack(new ItemStack(ModItems.JIEDAO));}
                    else if (0.82 <= i && i < 0.86) {user.giveItemStack(new ItemStack(ModItems.STEAL));}
                    else if (0.86 <= i && i < 0.90) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.90 <= i && i < 0.94) {user.giveItemStack(new ItemStack(ModItems.DISCARD));}
                    else if (0.94 <= i && i < 0.97) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.97 <= i) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else {user.giveItemStack(new ItemStack(ModItems.WUXIE));} //剩余0.06
                }
            }
            //摸牌
            if (user.getStackInHand(hand).getItem() == ModItems.GAIN_CARD) {
                if (user.isSneaking()) {m=user.getStackInHand(hand).getCount();} else {m=1;}
                for (int n = 0; n<m; n++) {
                    float i = new Random().nextFloat();
                    if (i < 0.29) {user.giveItemStack(new ItemStack(ModItems.SHAN));}
                    else if (0.29 <= i && i < 0.41) {user.giveItemStack(new ItemStack(ModItems.PEACH));}
                    else if (0.41 <= i && i < 0.5) {user.giveItemStack(new ItemStack(ModItems.JIU));}
                    else if (0.5 <= i && i < 0.51) {user.giveItemStack(new ItemStack(ModItems.GUDINGDAO));}
                    else if (0.51 <= i && i < 0.52) {user.giveItemStack(new ItemStack(ModItems.ARROW_RAIN));}
                    else if (0.52 <= i && i < 0.53) {user.giveItemStack(new ItemStack(ModItems.RATTAN_CHESTPLATE));}
                    else if (0.53 <= i && i < 0.54) {user.giveItemStack(new ItemStack(ModItems.RATTAN_LEGGINGS));}
                    else if (0.54 <= i && i < 0.55) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else if (0.55 <= i && i < 0.56) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.56 <= i && i < 0.57) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.57 <= i && i < 0.58) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.59 <= i && i < 0.64) {user.giveItemStack(new ItemStack(ModItems.WUZHONG));}
                    else if (0.64 <= i && i < 0.69) {user.giveItemStack(new ItemStack(ModItems.TIESUO));}
                    else if (0.74 <= i && i < 0.78) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.78 <= i && i < 0.82) {user.giveItemStack(new ItemStack(ModItems.JIEDAO));}
                    else if (0.82 <= i && i < 0.86) {user.giveItemStack(new ItemStack(ModItems.STEAL));}
                    else if (0.86 <= i && i < 0.90) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.90 <= i && i < 0.94) {user.giveItemStack(new ItemStack(ModItems.DISCARD));}
                    else if (0.94 <= i && i < 0.97) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.97 <= i) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else {user.giveItemStack(new ItemStack(ModItems.WUXIE));}
                    if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
                }
            }
            //无中生有
            if (user.getStackInHand(hand).getItem() == ModItems.WUZHONG) {
                for (int n = 0; n<2; n++) {
                    float i = new Random().nextFloat();
                    if (i < 0.29) {user.giveItemStack(new ItemStack(ModItems.SHAN));}
                    else if (0.29 <= i && i < 0.41) {user.giveItemStack(new ItemStack(ModItems.PEACH));}
                    else if (0.41 <= i && i < 0.5) {user.giveItemStack(new ItemStack(ModItems.JIU));}
                    else if (0.5 <= i && i < 0.51) {user.giveItemStack(new ItemStack(ModItems.GUDINGDAO));}
                    else if (0.51 <= i && i < 0.52) {user.giveItemStack(new ItemStack(ModItems.ARROW_RAIN));}
                    else if (0.52 <= i && i < 0.53) {user.giveItemStack(new ItemStack(ModItems.RATTAN_CHESTPLATE));}
                    else if (0.53 <= i && i < 0.54) {user.giveItemStack(new ItemStack(ModItems.RATTAN_LEGGINGS));}
                    else if (0.54 <= i && i < 0.55) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else if (0.55 <= i && i < 0.56) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.56 <= i && i < 0.57) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.57 <= i && i < 0.58) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.59 <= i && i < 0.64) {user.giveItemStack(new ItemStack(ModItems.WUZHONG));}
                    else if (0.64 <= i && i < 0.69) {user.giveItemStack(new ItemStack(ModItems.TIESUO));}
                    else if (0.74 <= i && i < 0.78) {user.giveItemStack(new ItemStack(ModItems.JUEDOU));}
                    else if (0.78 <= i && i < 0.82) {user.giveItemStack(new ItemStack(ModItems.JIEDAO));}
                    else if (0.82 <= i && i < 0.86) {user.giveItemStack(new ItemStack(ModItems.STEAL));}
                    else if (0.86 <= i && i < 0.90) {user.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
                    else if (0.90 <= i && i < 0.94) {user.giveItemStack(new ItemStack(ModItems.DISCARD));}
                    else if (0.94 <= i && i < 0.97) {user.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
                    else if (0.97 <= i) {user.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
                    else {user.giveItemStack(new ItemStack(ModItems.WUXIE));}
                }
                if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.WUZHONG, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
        }
        return super.use(world, user, hand);
    }
}
