package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowEntiytMixin extends ProjectileEntity {
    public ArrowEntiytMixin(EntityType<? extends ProjectileEntity> entityType, World world) {super(entityType, world);}

    @Shadow protected boolean inGround;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (getCommandTags().contains("a") && this.inGround) this.discard();
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setFireTicks(I)V"))
    private void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (getCommandTags().contains("a")) this.discard();
    }
}
