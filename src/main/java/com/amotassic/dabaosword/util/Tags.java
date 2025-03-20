package com.amotassic.dabaosword.util;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    private static TagKey<Item> createTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("dabaosword", name));
    }

    public static final TagKey<Item>
    SKILLS = trinketTag("head/skills"),
    WEAPON = trinketTag("hand/weapon"),
    ARMOR = trinketTag("chest/armor"),
    ATTACK = trinketTag("legs/attack"),
    DEFEND = trinketTag("legs/defend");

    private static TagKey<Item> trinketTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", name));
    }

    public static final TagKey<DamageType> TRIGGER_TIESUO = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dabaosword", "trigger_tiesuo"));

    public static void Tag() {}
}
