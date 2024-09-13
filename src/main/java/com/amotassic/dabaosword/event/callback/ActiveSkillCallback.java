package com.amotassic.dabaosword.event.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ActiveSkillCallback {
    Event<ActiveSkillCallback> EVENT = EventFactory.createArrayBacked(ActiveSkillCallback.class,
            listeners -> (player, stack, target) -> {
                for (ActiveSkillCallback listener: listeners){
                    listener.activeSkill(player, stack, target);
                }
            });

    /**
     * Called when a player who equipped an active skill presses the active skill key
     *
     * @param player The player trying to active skill
     * @param stack The active skill trinket itemstack
     * @param target The target player. If no target, the player is the target
     */
    void activeSkill(PlayerEntity player, ItemStack stack, PlayerEntity target);
}
