package com.amotassic.dabaosword;

import com.amotassic.dabaosword.command.InfoCommand;
import com.amotassic.dabaosword.event.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DabaoSword implements ModInitializer {
    public static final String MOD_ID = "dabaosword";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        ModItems.registerItems();
        ModItems.register();
        Sounds.sound();
        SkillCards.register();
        Tags.Tag();
        Gamerule.registerGamerules();
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
        EntityHurtCallback.EVENT.register(new EntityHurtHandler());
        ServerNetworking.registerActiveSkillPacketHandler();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> InfoCommand.register(dispatcher)));
        PlayerDeathCallback.EVENT.register(new PlayerDeathHandler());
        PlayerRespawnCallback.EVENT.register(new PlayerRespawnHandler());
        PlayerConnectCallback.EVENT.register(new PlayerConnectHandler());

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_SWORD,ModItems.GUDINGDAO);
            content.addAfter(Items.EGG,ModItems.ARROW_RAIN);
        });
    }
}
