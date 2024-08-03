package com.amotassic.dabaosword.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    public static class Items {

        public static final TagKey<Item> CARD = createTag("card");
        public static final TagKey<Item> BASIC_CARD = createTag("basic_card");
        public static final TagKey<Item> ARMOURY_CARD = createTag("armoury_card");
        public static final TagKey<Item> SKILL = createTag("skills");
        public static final TagKey<Item> SHA = createTag("sha");
        public static final TagKey<Item> RECOVER = createTag("recover");
        public static final TagKey<Item> LOCK_SKILL = createTag("lock_skill");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("dabaosword", name));
        }
    }
    public static void Tag() {}
}
