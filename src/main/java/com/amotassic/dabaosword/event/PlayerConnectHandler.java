package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.amotassic.dabaosword.item.card.GiftBoxItem.selectRandomEntry;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerConnectHandler implements PlayerConnectCallback {
    @Override
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {

        if (!player.getCommandTags().contains("given_skill")) {
            changeSkill(player);
            player.addCommandTag("given_skill");
        }

    }

    public void changeSkill(PlayerEntity player) {
        List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/change_skill.json"));
        LootEntry selectedEntry = selectRandomEntry(lootEntries);

        ItemStack stack = new ItemStack(Registries.ITEM.get(selectedEntry.item()));
        if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
        player.giveItemStack(stack);
    }
}
