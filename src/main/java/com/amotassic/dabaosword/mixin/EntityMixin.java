package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.amotassic.dabaosword.util.ModTools.*;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        this.damage(getDamageSource(lightning, DamageTypes.LIGHTNING_BOLT), 5.0f);
        ci.cancel();
    }
}

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    @Shadow public abstract void resetPickupDelay();

    @Unique
    ItemEntity thisItem = (ItemEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (isCard(this.getStack())) this.resetPickupDelay();

        ItemStack stack = this.getStack();
        var entity = getClosestEntity(thisItem, Entity.class, 0.2, e -> true);
        if (stack.isOf(Items.ARROW) && stack.getCount() == 64) {
            if (entity instanceof ItemEntity item && item.getStack().isOf(Items.BOW)) {
                item.setStack(new ItemStack(ModItems.ARROW_RAIN));
                this.discard();
            }
        }

        if (stack.isOf(Items.EMERALD) && stack.getCount() == 64) {
            if (entity instanceof VillagerEntity villager && villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                this.setStack(new ItemStack(ModItems.GIFTBOX, 1));
            }
        }
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
