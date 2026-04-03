package com.amotassic.dabaosword.entity;

import com.amotassic.dabaosword.DabaoSword;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntity {
    public static final EntityType<XuyouEntity> XUYOU = register("xuyou", EntityType.Builder.of(XuyouEntity::new, MobCategory.MONSTER).sized(0.6f, 1.8f));

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        var key = ResourceKey.create(Registries.ENTITY_TYPE, DabaoSword.id(id));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(XUYOU, XuyouEntity.createAttributes());
        BiomeModifications.addSpawn(BiomeSelectors.spawnsOneOf(EntityType.ZOMBIE), MobCategory.MONSTER, XUYOU, 15, 1, 1);
        SpawnPlacements.register(XUYOU, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }
}
