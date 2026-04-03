package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.DabaoSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class Tags {

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, DabaoSword.id(id));
    }

    public static final TagKey<Item>
            SKILLS = trinketTag("head/skills"),
            WEAPON = trinketTag("hand/weapon"),
            ARMOR = trinketTag("chest/armor"),
            ATTACK = trinketTag("legs/attack"),
            DEFEND = trinketTag("legs/defend");

    private static TagKey<Item> trinketTag(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("trinkets", name));
    }

    public static final TagKey<DamageType> TRIGGER_TIESUO = TagKey.create(Registries.DAMAGE_TYPE, DabaoSword.id("trigger_tiesuo"));

    public static final TagKey<DamageType> FROM_CARD = TagKey.create(Registries.DAMAGE_TYPE, DabaoSword.id("from_card"));

    public static void Tag() {}
}
