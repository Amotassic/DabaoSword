package com.amotassic.dabaosword.damage_type;

import com.amotassic.dabaosword.DabaoSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class ModDT {

    public static final ResourceKey<DamageType>
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

    private static DamageSource create(Entity entity, ResourceKey<DamageType> key, boolean... notFromEntity) {
        boolean bl = notFromEntity.length > 0 && notFromEntity[0];
        var entry = entity.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
        Entity source = bl ? null : entity;
        return new DamageSource(entry, source);
    }

    private static ResourceKey<DamageType> register(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, DabaoSword.id(id));
    }

    public static void init() {}
}
