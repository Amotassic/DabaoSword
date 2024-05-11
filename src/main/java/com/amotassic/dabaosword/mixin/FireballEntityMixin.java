package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.util.Gamerule;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireballEntity.class)
public abstract class FireballEntityMixin extends AbstractFireballEntity {

    @Shadow private int explosionPower;

    public FireballEntityMixin(EntityType<? extends AbstractFireballEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        boolean bl = !this.getWorld().getGameRules().getBoolean(Gamerule.FIRE_ATTACK_BREAKS_BLOCK);
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if (bl && this.explosionPower == 3) {
                this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 3, false, World.ExplosionSourceType.NONE);
                this.discard();
                ci.cancel();
            }
        }
    }
}
