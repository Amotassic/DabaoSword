package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends PersistentProjectileEntity {
    protected ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
    //清除万箭齐发产生的箭
    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!this.getWorld().isClient() && this.inGround && this.getCustomName() == Text.of("arrow_rain")) {
            this.getWorld().sendEntityStatus(this, (byte)0);
        }
    }
}
