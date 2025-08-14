package com.amotassic.dabaosword;

import com.amotassic.dabaosword.api.config.Configuration;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.data.CardSuitAndRank;
import com.amotassic.dabaosword.entity.ModEntity;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModConfig;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DabaoSword implements ModInitializer {
    public static final String MOD_ID = "dabaosword";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;
    @Override
    public void onInitialize() {
        LOGGER.info("Ciallo～(∠·ω< )⌒★");
        new Configuration(ModConfig.class, MOD_ID);
        ModItems.register();
        long start = System.currentTimeMillis();
        SkillCards.register();
        LOGGER.info("Loaded all skills in {}ms", System.currentTimeMillis() - start);
        ResourceManagerHelper helper = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
        helper.registerReloadListener(CardSuitAndRank.INSTANCE);
        ModDT.init();
        Tags.Tag();
        Gamerule.registerGamerules();
        ServerNetworking.registerActiveSkill();
        CommandRegistrationCallback.EVENT.register(((d, a, e) -> DabaoSwordCommand.register(d,a)));
        ModEntity.register();
        ModEntity.entitySpawn();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_SWORD,ModItems.GUDINGDAO);
            content.addAfter(Items.EGG,ModItems.ARROW_RAIN);
        });
    }
}
