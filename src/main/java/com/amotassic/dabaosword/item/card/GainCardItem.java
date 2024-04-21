package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class GainCardItem extends CardItem implements ModTools {
    public GainCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int m;
            //牌堆，潜行时直接摸64张
            if (user.getStackInHand(hand).getItem() == ModItems.CARD_PILE) {
                if (user.isCreative()) {
                    if (user.isSneaking()) {m=64;} else {m=1;}
                    draw(user,m);voice(user, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
                }
            }
            //摸牌
            if (user.getStackInHand(hand).getItem() == ModItems.GAIN_CARD) {
                if (user.isSneaking()) {m=user.getStackInHand(hand).getCount();} else {m=1;}
                draw(user,m);voice(user, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
                if (!user.isCreative()) {user.getStackInHand(hand).decrement(m);}
            }
            //无中生有
            if (user.getStackInHand(hand).getItem() == ModItems.WUZHONG) {
                draw(user,2);
                if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
                voice(user, Sounds.WUZHONG);
                jizhi(user);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void draw(PlayerEntity player, int count) {
        for (int n = 0; n<count; n++) {
            float i = new Random().nextFloat();
            if (i < 0.29) {player.giveItemStack(new ItemStack(ModItems.SHAN));}
            else if (0.29 <= i && i < 0.41) {player.giveItemStack(new ItemStack(ModItems.PEACH));}
            else if (0.41 <= i && i < 0.5) {player.giveItemStack(new ItemStack(ModItems.JIU));}
            else if (0.5 <= i && i < 0.505) {player.giveItemStack(new ItemStack(ModItems.GUDINGDAO));}
            else if (0.505 <= i && i < 0.51) {player.giveItemStack(new ItemStack(ModItems.QINGGANG));}
            else if (0.51 <= i && i < 0.52) {player.giveItemStack(new ItemStack(ModItems.WANJIAN));}
            else if (0.52 <= i && i < 0.53) {player.giveItemStack(new ItemStack(ModItems.RATTAN_CHESTPLATE));}
            else if (0.53 <= i && i < 0.54) {player.giveItemStack(new ItemStack(ModItems.RATTAN_LEGGINGS));}
            else if (0.54 <= i && i < 0.57) {player.giveItemStack(new ItemStack(ModItems.NANMAN));}
            else if (0.57 <= i && i < 0.59) {player.giveItemStack(new ItemStack(ModItems.DILU));}
            else if (0.59 <= i && i < 0.61) {player.giveItemStack(new ItemStack(ModItems.CHITU));}
            else if (0.61 <= i && i < 0.65) {player.giveItemStack(new ItemStack(ModItems.WUZHONG));}
            else if (0.65 <= i && i < 0.69) {player.giveItemStack(new ItemStack(ModItems.TIESUO));}
            else if (0.74 <= i && i < 0.78) {player.giveItemStack(new ItemStack(ModItems.JUEDOU));}
            else if (0.78 <= i && i < 0.82) {player.giveItemStack(new ItemStack(ModItems.JIEDAO));}
            else if (0.82 <= i && i < 0.83) {player.giveItemStack(new ItemStack(ModItems.TAOYUAN));}
            else if (0.83 <= i && i < 0.86) {player.giveItemStack(new ItemStack(ModItems.STEAL));}
            else if (0.86 <= i && i < 0.90) {player.giveItemStack(new ItemStack(ModItems.FIRE_ATTACK));}
            else if (0.90 <= i && i < 0.93) {player.giveItemStack(new ItemStack(ModItems.DISCARD));}
            else if (0.94 <= i && i < 0.97) {player.giveItemStack(new ItemStack(ModItems.TOO_HAPPY_ITEM));}
            else if (0.97 <= i) {player.giveItemStack(new ItemStack(ModItems.BINGLIANG_ITEM));}
            else {player.giveItemStack(new ItemStack(ModItems.WUXIE));}//剩余0.06，非最终版本，还会改
        }
    }
}
