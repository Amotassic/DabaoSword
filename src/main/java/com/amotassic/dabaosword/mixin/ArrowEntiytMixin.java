package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntiytMixin extends PersistentProjectileEntity {
    protected ArrowEntiytMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (getCommandTags().contains("a") && this.inGround) this.discard();
    }
}
