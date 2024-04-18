package com.amotassic.dabaosword.item;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class LoseHPDamageSource {
    public Registry<DamageType> registry;
    private DamageSource losehp;

    public void DamageSources(DynamicRegistryManager registryManager) {
        this.registry = registryManager.get(RegistryKeys.DAMAGE_TYPE);
        this.losehp = this.create(DabaoSwordDamageTypes.LOSE_HP);
    }

    public LoseHPDamageSource(Registry<DamageType> registry, DamageSource loseHp) {
        this.registry = registry;
        losehp = loseHp;
    }

    public final DamageSource create(RegistryKey<DamageType> key) {
        return new DamageSource(this.registry.entryOf(key));
    }

    public DamageSource LOSE_HP() {
        return this.losehp;
    }

    public static void register() {}
}
