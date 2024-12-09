package com.amotassic.dabaosword.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntity {
    public static final EntityType<XuyouEntity> XUYOU = Registry.register(Registries.ENTITY_TYPE, new Identifier("dabaosword", "xuyou"), EntityType.Builder.create(XuyouEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.8f).build("xuyou"));

    public static void register() {
        FabricDefaultAttributeRegistry.register(XUYOU, XuyouEntity.createAttributes());
    }

    public static void entitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE), SpawnGroup.MONSTER, XUYOU, 5, 1, 1);
        SpawnRestriction.register(XUYOU, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }
}
