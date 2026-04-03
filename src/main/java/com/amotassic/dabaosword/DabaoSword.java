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
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
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
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(CardSuitAndRank.ID, new CardSuitAndRank());
        ModDT.init();
        Tags.Tag();
        Gamerule.registerGamerules();
        ServerNetworking.register();
        CommandRegistrationCallback.EVENT.register(DabaoSwordCommand::register);
        ModEntity.register();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(content -> {
            content.insertAfter(Items.NETHERITE_SWORD, ModItems.GUDINGDAO);
            content.insertBefore(Items.BOW, ModItems.ARROW_RAIN);
        });
    }

    public static Identifier id(String path) {return Identifier.fromNamespaceAndPath(MOD_ID, path);}
}
