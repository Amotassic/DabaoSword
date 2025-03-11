package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Inject(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        this.damage(world, ModTools.damageSource(lightning, DamageTypes.LIGHTNING_BOLT), 5.0f);
        ci.cancel();
    }
}
