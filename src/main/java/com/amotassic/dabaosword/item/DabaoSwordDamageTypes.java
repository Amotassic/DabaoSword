package com.amotassic.dabaosword.item;

import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DabaoSwordDamageTypes extends DamageTypes {
    public static final RegistryKey<DamageType> LOSE_HP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("lose_hp"));

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(LOSE_HP, new DamageType("lose_hp", 0.0f, DamageEffects.HURT));
    }
}
