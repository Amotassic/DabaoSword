package com.amotassic.dabaosword.util;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Tags {

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("dabaosword", id));
    }

    public static final TagKey<Item>
            SKILLS = trinketTag("head/skills"),
            WEAPON = trinketTag("hand/weapon"),
            ARMOR = trinketTag("chest/armor"),
            ATTACK = trinketTag("legs/attack"),
            DEFEND = trinketTag("legs/defend");

    private static TagKey<Item> trinketTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("trinkets", name));
    }

    public static final TagKey<DamageType> TRIGGER_TIESUO = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("dabaosword", "trigger_tiesuo"));

    public static final TagKey<DamageType> FROM_CARD = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("dabaosword", "from_card"));

    public static void Tag() {}
}
