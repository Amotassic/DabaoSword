package com.amotassic.dabaosword.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    public static class Items {

        public static final TagKey<Item> CARD = Items.createTag("card");
        public static final TagKey<Item> BASIC_CARD = Items.createTag("basic_card");
        public static final TagKey<Item> ARMOURY_CARD = Items.createTag("armoury_card");
        public static final TagKey<Item> SKILL = Items.createTag("skill");
        public static final TagKey<Item> SHA = Items.createTag("sha");
        public static final TagKey<Item> RECOVER = Items.createTag("recover");


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("dabaosword", name));
        }
    }
    public static void Tag() {}
}
