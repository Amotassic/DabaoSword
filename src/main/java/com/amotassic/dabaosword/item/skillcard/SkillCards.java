package com.amotassic.dabaosword.item.skillcard;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillCards {

    public static final Item YIJI = new YijiSkill(new FabricItemSettings().maxDamage(2));

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "yiji"), YIJI);
    }
}
