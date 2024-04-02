package com.amotassic.dabaosword;

import com.amotassic.dabaosword.event.AttackEntityHandler;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.fabricmc.api.ModInitializer;
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
        ModItems.registerModItems();
        ModItems.register();
        Sounds.sound();
        SkillCards.register();
        Tags.Tag();
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.NETHERITE_SWORD,ModItems.GUDINGDAO);
            content.addAfter(Items.LEATHER_BOOTS,ModItems.RATTAN_CHESTPLATE);
            content.addAfter(ModItems.RATTAN_CHESTPLATE,ModItems.RATTAN_LEGGINGS);
            content.addAfter(Items.EGG,ModItems.ARROW_RAIN);
        });
    }
}
