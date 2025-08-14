package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModConfig;
import com.amotassic.dabaosword.util.ModTools;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
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

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        if (lightning.getCommandTags().contains("a")) {
            this.damage(ModDT.shandian(lightning), 5.0f);
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "occludeVibrationSignals", at = @At("RETURN"))
    public boolean occludeVibrationSignals(boolean original) {
        if ((Entity) (Object) this instanceof PlayerEntity player && player.getCommandTags().contains("wuyan")) {
            return true;
        }
        return original;
    }
}

@Mixin(PersistentProjectileEntity.class)
abstract class ArrowEntiytMixin extends ProjectileEntity {
    public ArrowEntiytMixin(EntityType<? extends ProjectileEntity> entityType, World world) {super(entityType, world);}

    @Shadow protected boolean inGround;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        var tags = getCommandTags();
        if (this.inGround) {
            if (tags.contains("a") || tags.contains("cosmetic")) this.discard();
        }
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setFireTicks(I)V"))
    private void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (getCommandTags().contains("a")) this.discard();
    }

    @Inject(method = "onEntityHit", at = @At(value = "HEAD"), cancellable = true)
    private void cosmetic(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (getCommandTags().contains("cosmetic")) {
            this.discard(); ci.cancel();
        }
    }
}

@Mixin(FireballEntity.class)
abstract class FireballEntityMixin extends AbstractFireballEntity {
    public FireballEntityMixin(EntityType<? extends AbstractFireballEntity> entityType, World world) {super(entityType, world);}

    @ModifyArgs(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"))
    public void onCollision(Args args) {
        if (!ModConfig.FireAttackBreaksBlock && getCommandTags().contains("a")) {
            args.set(5, false);
            args.set(6, World.ExplosionSourceType.NONE);
        }
    }

    @ModifyArgs(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    protected void onEntityHit(Args args) {
        if (getCommandTags().contains("a") && getOwner() != null) {
            getOwner().addCommandTag("sha"); //防止触发杀
            args.set(0, ModDT.huogong(getOwner()));
            args.set(1, 6f);
        }
    }
}

@Mixin(Explosion.class)
abstract class ExplosionMixin {

    @Shadow @Final
    private @Nullable Entity entity;

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public DamageSource collectBlocksAndDamageEntities(DamageSource source) {
        if (entity instanceof FireballEntity fireball && entity.getCommandTags().contains("a") && fireball.getOwner() != null) {
            fireball.getOwner().addCommandTag("sha"); //防止触发杀
            return ModDT.huogong(fireball.getOwner());
        }
        return source;
    }
}

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    @Shadow public abstract void resetPickupDelay();

    @Shadow private @Nullable UUID owner;
    @Unique
    ItemEntity thisItem = (ItemEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ModTools.isCard(this.getStack())) this.resetPickupDelay();
        if (getWorld() instanceof ServerWorld world) {
            Entity follow = world.getEntity(owner);
            if (world.getTime() % 20 == 0 && getCommandTags().contains("follow_owner") && follow != null) {
                teleport((ServerWorld) follow.getWorld(), follow.getX(), follow.getY(), follow.getZ(), new HashSet<>(), getPitch(), getYaw());
            }
        }

        ItemStack stack = this.getStack();
        if (stack.isOf(Items.ARROW) && stack.getCount() == 64) {
            var entity = ModTools.getClosestEntity(thisItem, Entity.class, 0.2, e -> true);
            if (entity instanceof ItemEntity item && item.getStack().isOf(Items.BOW)) {
                item.setStack(new ItemStack(ModItems.ARROW_RAIN));
                this.discard();
            }
        }

        if (stack.isOf(Items.EMERALD) && stack.getCount() == 64) {
            var entity = ModTools.getClosestEntity(thisItem, Entity.class, 0.2, e -> true);
            if (entity instanceof VillagerEntity villager && villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                this.setStack(new ItemStack(ModItems.GIFTBOX, 1));
            }
        }
    }
}

@Mixin(TridentEntity.class)
abstract class TridentMixin extends PersistentProjectileEntity {
    protected TridentMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (getCommandTags().contains("a")) this.discard();
    }
}
