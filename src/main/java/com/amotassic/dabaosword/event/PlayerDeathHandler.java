package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.*;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;

public class PlayerDeathHandler implements PlayerDeathCallback, ModTools {
    @Override
    public void onDeath(ServerPlayerEntity player, DamageSource source) {

        if (player.getWorld() instanceof ServerWorld world) {
            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) {
                    ItemStack stack = inv.getStack(i);
                    if (stack.isIn(Tags.Items.CARD) || stack.getItem() == ModItems.GAIN_CARD) inv.removeStack(i);
                }

                List<ItemStack> armors = (List<ItemStack>) player.getArmorItems();
                for (ItemStack stack : armors) {
                    if (!stack.isEmpty() && stack.isIn(Tags.Items.CARD)) stack.setCount(0);
                }

                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack = entry.getRight();
                        if(stack.isIn(Tags.Items.CARD)) stack.setCount(0);
                    }
                }
            }

            if (hasItem(player, ModItems.BBJI)) voice(player, Sounds.XUYOU);
        }
    }
}
