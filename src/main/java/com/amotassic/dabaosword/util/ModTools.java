package com.amotassic.dabaosword.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface ModTools {
    //检测玩家是否有某个物品
    default boolean hasItem(@NotNull PlayerEntity player, @NotNull Item item) {
        PlayerInventory inv = player.getInventory();
        ItemStack stack = item.getDefaultStack();
        return inv.contains(stack);
    }
    //移除玩家的1个物品
    default void removeItem(@NotNull PlayerEntity player, @NotNull Item item) {
        PlayerInventory inv = player.getInventory();
        ItemStack stack = item.getDefaultStack();
        int i = inv.getSlotWithStack(stack);
        inv.removeStack(i, 1);
    }

    default ItemStack stackWith(Item item, PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        return inv.getStack(i);
    }
    //播放语音
    default void voice(@NotNull PlayerEntity player, SoundEvent sound) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }
    //视为类技能方法
    default void viewAs(@NotNull PlayerEntity player, Item item, SoundEvent sound1, SoundEvent sound2) {
        ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
        if (!stack.isEmpty() && stack.isIn(Tags.Items.BASIC_CARD)) {
            stack.decrement(1);
            player.giveItemStack(item.getDefaultStack());
            if (new Random().nextFloat() < 0.5) {voice(player, sound1);} else {voice(player, sound2);}
        }
    }
}
