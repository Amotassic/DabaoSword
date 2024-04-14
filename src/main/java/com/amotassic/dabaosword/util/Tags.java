package com.amotassic.dabaosword.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    public static class Items {

        public static final TagKey<Item> CARD = Tags.Items.createTag("card");
        public static final TagKey<Item> BASIC_CARD = Tags.Items.createTag("basic_card");
        public static final TagKey<Item> SKILL = Tags.Items.createTag("skill");


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("dabaosword", name));
        }
    }
    public static void Tag() {}
}
