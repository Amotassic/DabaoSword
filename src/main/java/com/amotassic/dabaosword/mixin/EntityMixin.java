package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashSet;
import java.util.UUID;

import static com.amotassic.dabaosword.util.ModTools.getClosestEntity;
import static com.amotassic.dabaosword.util.ModTools.isCard;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean hurtServer(ServerLevel level, DamageSource source, float damage);

    @Inject(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"), cancellable = true)
    public void onStruckByLightning(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        if (lightning.entityTags().contains("a")) {
            this.hurtServer(level, ModDT.shandian(lightning), 5.0f);
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "dampensVibrations", at = @At("RETURN"))
    public boolean occludeVibrationSignals(boolean original) {
        if ((Entity) (Object) this instanceof Player player && player.entityTags().contains("wuyan")) {
            return true;
        }
        return original;
    }
}

@Mixin(AbstractArrow.class)
abstract class ArrowEntiytMixin extends Projectile {
    public ArrowEntiytMixin(EntityType<? extends Projectile> entityType, Level world) {super(entityType, world);}

    @Shadow protected abstract boolean isInGround();

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        var tags = entityTags();
        if (this.isInGround()) {
            if (tags.contains("a") || tags.contains("cosmetic")) this.discard();
        }
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V"))
    private void onHit(EntityHitResult hitResult, CallbackInfo ci) {
        if (entityTags().contains("a")) this.discard();
    }

    @Inject(method = "onHitEntity", at = @At(value = "HEAD"), cancellable = true)
    private void cosmetic(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (entityTags().contains("cosmetic")) {
            this.discard(); ci.cancel();
        }
    }
}

@Mixin(LargeFireball.class)
abstract class FireballEntityMixin extends Fireball {
    public FireballEntityMixin(EntityType<? extends Fireball> entityType, Level world) {super(entityType, world);}

    @ModifyArgs(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    public void onCollision(Args args) {
        if (level() instanceof ServerLevel) {
            if (!ModConfig.FireAttackBreaksBlock && entityTags().contains("a")) {
                args.set(5, false);
                args.set(6, Level.ExplosionInteraction.NONE);
            }
        }
    }

    @ModifyArgs(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    protected void onEntityHit(Args args) {
        if (entityTags().contains("a") && getOwner() != null) {
            getOwner().addTag("sha"); //防止触发杀
            args.set(1, ModDT.huogong(this.getOwner()));
            args.set(2, 6f);
        }
    }
}

@Mixin(ServerExplosion.class)
abstract class ExplosionMixin {

    @Shadow @Final private @Nullable Entity source;

    @ModifyArg(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public DamageSource collectBlocksAndDamageEntities(DamageSource damageSource) {
        if (source instanceof LargeFireball fireball && source.entityTags().contains("a") && fireball.getOwner() != null) {
            fireball.getOwner().addTag("sha"); //防止触发杀
            return ModDT.huogong(fireball.getOwner());
        }
        return damageSource;
    }
}

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, Level world) {super(type, world);}

    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract void setItem(ItemStack itemStack);

    @Shadow public abstract void setNoPickUpDelay();

    @Shadow private @Nullable UUID target;
    @Unique
    ItemEntity thisItem = (ItemEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (isCard(this.getItem())) this.setNoPickUpDelay();
        if (level() instanceof ServerLevel world && target != null) {
            Entity follow = world.getEntity(target);
            if (world.getGameTime() % 20 == 0 && entityTags().contains("follow_owner") && follow != null) {
                teleportTo((ServerLevel) follow.level(), follow.getX(), follow.getY(), follow.getZ(), new HashSet<>(), getYRot(), getXRot(), false);
            }
        }

        ItemStack stack = this.getItem();
        if (stack.is(Items.ARROW) && stack.getCount() == 64) {
            var entity = getClosestEntity(thisItem, Entity.class, 0.2, e -> true);
            if (entity instanceof ItemEntity item && item.getItem().is(Items.BOW)) {
                item.setItem(new ItemStack(ModItems.ARROW_RAIN));
                this.discard();
            }
        }

        if (stack.is(Items.EMERALD) && stack.getCount() == 64) {
            var entity = getClosestEntity(thisItem, Entity.class, 0.2, e -> true);
            if (entity instanceof Villager villager && villager.getVillagerData().profession().unwrapKey().orElse(VillagerProfession.NONE) == VillagerProfession.NITWIT) {
                this.setItem(new ItemStack(ModItems.GIFTBOX, 1));
            }
        }
    }
}

@Mixin(ThrownTrident.class)
abstract class TridentMixin extends AbstractArrow {
    protected TridentMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (entityTags().contains("a")) this.discard();
    }
}
