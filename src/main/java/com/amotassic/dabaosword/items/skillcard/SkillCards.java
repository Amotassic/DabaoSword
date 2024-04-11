package com.amotassic.dabaosword.items.skillcard;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillCards {

    public static final Item YIJI = register("yiji",new YijiSkill(new FabricItemSettings().maxDamage(2)));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier("dabaosword",name),item);
    }

    public static void register() {}
}
