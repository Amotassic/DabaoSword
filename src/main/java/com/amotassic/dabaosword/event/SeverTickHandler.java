package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SeverTickHandler implements ServerTickEvents.EndTick, ModTools {
    private int tick = 0;
    @Override
    public void onEndTick(MinecraftServer server) {
        if (++tick >= 1200) { // 每分钟检查一次
            tick = 0;
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    if (hasTrinket(ModItems.CARD_PILE, player) && !player.isCreative() && !player.isSpectator()) {
                        player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                        player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    }
                }
            }
        }
    }
}
