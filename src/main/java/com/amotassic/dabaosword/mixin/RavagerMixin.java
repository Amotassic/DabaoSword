package com.amotassic.dabaosword.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends Raider {
    @Shadow private int stunnedTick;

    @Shadow private int roarTick;

    @Shadow protected abstract void strongKnockback(Entity entity);

    protected RavagerMixin(EntityType<? extends Raider> entityType, Level world) {super(entityType, world);}

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (entityTags().contains("a")) {
            stunnedTick = 20;
            entityTags().remove("a");
        }
        if (entityTags().contains("b") && roarTick == 1) discard();
    }

    @Inject(method = "roar", at = @At("HEAD"), cancellable = true)
    private void roar(CallbackInfo ci) {
        if (isAlive() && hasCustomName() && entityTags().contains("b")) {
            int id = Integer.parseInt(Objects.requireNonNull(getCustomName()).getString());
            LivingEntity living = (LivingEntity) level().getEntity(id);
            if (living == null) {ci.cancel(); return;}
            strongKnockback(living);
            ci.cancel();
        }
    }
}
