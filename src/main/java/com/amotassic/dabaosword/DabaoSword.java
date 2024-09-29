package com.amotassic.dabaosword;

import com.amotassic.dabaosword.command.InfoCommand;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
        LOGGER.info("Ciallo～(∠·ω< )⌒★");
        ModItems.register();
        Sounds.sound();
        SkillCards.register();
        Tags.Tag();
        Gamerule.registerGamerules();
        ServerNetworking.registerActiveSkill();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> InfoCommand.register(dispatcher)));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_SWORD,ModItems.GUDINGDAO);
            content.addAfter(Items.EGG,ModItems.ARROW_RAIN);
        });
    }
}
