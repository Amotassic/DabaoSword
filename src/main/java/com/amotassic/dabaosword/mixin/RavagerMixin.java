package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RavagerEntity.class)
public abstract class RavagerMixin extends RaiderEntity {
    @Shadow private int stunTick;

    @Shadow private int roarTick;

    @Shadow protected abstract void knockBack(Entity entity);

    protected RavagerMixin(EntityType<? extends RaiderEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (getCommandTags().contains("a")) {
            stunTick = 20;
            getCommandTags().remove("a");
        }
        if (getCommandTags().contains("b") && roarTick == 1) discard();
    }

    @Inject(method = "roar", at = @At("HEAD"), cancellable = true)
    private void roar(CallbackInfo ci) {
        if (isAlive() && hasCustomName() && getCommandTags().contains("b")) {
            int id = Integer.parseInt(Objects.requireNonNull(getCustomName()).getString());
            LivingEntity living = (LivingEntity) getEntityWorld().getEntityById(id);
            if (living == null) {ci.cancel(); return;}
            knockBack(living);
            ci.cancel();
        }
    }
}
