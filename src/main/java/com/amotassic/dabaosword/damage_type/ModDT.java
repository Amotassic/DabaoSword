package com.amotassic.dabaosword.damage_type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDT {

    public static final RegistryKey<DamageType>
    LOSEHP = register("losehp"),
    BBLL = register("bbll"),

    SHA = register("sha"),
    SHA_FIRE = register("sha_fire"),
    SHA_THUNDER = register("sha_thunder"),
    HUOGONG = register("huogong"),
    JUEDOU = register("juedou"),
    NANMAN = register("nanman"),
    WANJIAN = register("wanjian"),
    SHANDIAN = register("shandian");

    public static DamageSource loseHP(Entity entity) {return create(entity, LOSEHP, true);}
    public static DamageSource bbll(Entity entity) {return create(entity, BBLL);}

    public static DamageSource sha(Entity entity) {return create(entity, SHA);}
    public static DamageSource shaFire(Entity entity) {return create(entity, SHA_FIRE);}
    public static DamageSource shaThunder(Entity entity) {return create(entity, SHA_THUNDER);}
    public static DamageSource huogong(Entity entity) {return create(entity, HUOGONG);}
    public static DamageSource juedou(Entity entity) {return create(entity, JUEDOU);}
    public static DamageSource nanman(Entity entity) {return create(entity, NANMAN);}
    public static DamageSource wanjian(Entity entity) {return create(entity, WANJIAN);}
    public static DamageSource shandian(Entity entity) {return create(entity, SHANDIAN);}

    private static DamageSource create(Entity entity, RegistryKey<DamageType> key, boolean... notFromEntity) {
        boolean bl = notFromEntity.length > 0 && notFromEntity[0];
        var entry = entity.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key);
        Entity source = bl ? null : entity;
        return new DamageSource(entry, source);
    }

    private static RegistryKey<DamageType> register(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dabaosword", id));
    }

    public static void init() {}
}
