package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.PlayerRespawnCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerRespawnHandler implements PlayerRespawnCallback {
    @Override
    public void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity player) {

        if (player.getWorld() instanceof ServerWorld world) {

            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card) {
                player.giveItemStack(new ItemStack(ModItems.SHA));
                player.giveItemStack(new ItemStack(ModItems.SHAN));
                player.giveItemStack(new ItemStack(ModItems.PEACH));
            }

        }
    }
}
