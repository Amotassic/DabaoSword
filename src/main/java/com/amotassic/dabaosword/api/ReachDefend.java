package com.amotassic.dabaosword.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ReachDefend {

    default int getExtraReach(PlayerEntity player, ItemStack stack) {return 0;}

    default int getDefend(PlayerEntity player, ItemStack stack) {return 0;}
}
