package com.amotassic.dabaosword.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class Tags {

    public static class Items {

        public static final TagKey<Item> CARD = of("card");
        public static final TagKey<Item> BASIC_CARD = of("basic_card");
        public static final TagKey<Item> ARMOURY_CARD = of("armoury_card");
        public static final TagKey<Item> SKILL = of("skill");
        public static final TagKey<Item> SHA = of("sha");
        public static final TagKey<Item> RECOVER = of("recover");
        public static final TagKey<Item> LOCK_SKILL = of("lock_skill");

        private static TagKey<Item> of(String id) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of("dabaosword", id));
        }
    }
    public static void Tag() {}
}
