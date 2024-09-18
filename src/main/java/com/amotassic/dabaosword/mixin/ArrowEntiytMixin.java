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

import java.util.Objects;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntiytMixin extends PersistentProjectileEntity {
    protected ArrowEntiytMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (Objects.equals(this.getCustomName(), Text.of("a")) && this.inGround) this.discard();
    }
}
