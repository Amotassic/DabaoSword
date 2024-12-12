package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.api.event.CardEvents.hurtBy;
import static com.amotassic.dabaosword.api.event.CardEvents.notHurtBy;

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
            LivingEntity user = (LivingEntity) getWorld().getEntityById(id);
            if (user == null) return;
            Predicate<LivingEntity> target = e -> e.isAlive() && id != e.getId();
            for (LivingEntity entity : getWorld().getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(7.0), target)) {
                user.addCommandTag("nanman");
                DamageSource source = getDamageSources().mobAttack(user);
                if (notHurtBy(entity, ModItems.NANMAN)) continue;
                entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 2, 0, false, false));
                if (entity.damage(source, 6)) hurtBy(entity, ModItems.NANMAN);
                knockBack(entity);
            }
            ci.cancel();
        }
    }
}
