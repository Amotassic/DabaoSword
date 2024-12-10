package com.amotassic.dabaosword.util;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    public static final TagKey<Item> LOCK_SKILL = createTag("lock_skill");
    public static final TagKey<Item> TRIGGER_WUXIE = createTag("trigger_wuxie");

    private static TagKey<Item> createTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("dabaosword", name));
    }

    public static final TagKey<DamageType> TRIGGER_TIESUO = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dabaosword", "trigger_tiesuo"));

    public static void Tag() {}
}
