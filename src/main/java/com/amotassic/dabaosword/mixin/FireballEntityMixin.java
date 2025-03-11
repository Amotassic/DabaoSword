package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FireballEntity.class)
public abstract class FireballEntityMixin extends AbstractFireballEntity {
    public FireballEntityMixin(EntityType<? extends AbstractFireballEntity> entityType, World world) {super(entityType, world);}

    @ModifyArgs(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"))
    public void onCollision(Args args) {
        if (!ModConfig.FireAttackBreaksBlock && getCommandTags().contains("a")) {
            args.set(5, false);
            args.set(6, World.ExplosionSourceType.NONE);
        }
    }
}
