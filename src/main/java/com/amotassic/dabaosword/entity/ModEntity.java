package com.amotassic.dabaosword.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntity {
    public static final EntityType<XuyouEntity> XUYOU = register("xuyou", EntityType.Builder.create(XuyouEntity::new, SpawnGroup.MONSTER).dimensions(0.6f, 1.8f));

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        var key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("dabaosword", id));
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(XUYOU, XuyouEntity.createAttributes());
    }

    public static void entitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE), SpawnGroup.MONSTER, XUYOU, 15, 1, 1);
        SpawnRestriction.register(XUYOU, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }
}
