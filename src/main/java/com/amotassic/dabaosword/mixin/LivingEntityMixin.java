package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModifyDamage;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {super(type, level);}

    @Shadow public abstract boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage);

    @Shadow public abstract @Nullable AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Unique LivingEntity living = (LivingEntity) (Object) this;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void damageMixin(ServerLevel world, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        //恭喜你发现了彩蛋！副手拿着幽匿催发体，然后尽情享受弹射物带来的快乐吧！
        if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getEntity() instanceof LivingEntity attacker && attacker.getOffhandItem().is(Items.SCULK_CATALYST)) {
            Vec3 vec3d = attacker.position().add(0.0, 1.5f, 0.0);
            Vec3 vec3d2 = this.getEyePosition().subtract(vec3d);
            Vec3 vec3d3 = vec3d2.normalize();
            for (int i = 1; i < Mth.floor(vec3d2.length()) + 7; ++i) {
                Vec3 vec3d4 = vec3d.add(vec3d3.scale(i));
                world.sendParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, 1.0F);
            this.hurtServer(world, world.damageSources().sonicBoom(attacker), 10.0f);
            double d = 0.5 * (1.0 - this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue());
            double e = 2.5 * (1.0 - this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue());
            this.setDeltaMovement(vec3d3.x * e, vec3d3.y * d, vec3d3.z * e);
        }
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void cancelDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        int i = ModifyDamage.shouldCancel(living, source, damage);
        if (i == 1) cir.setReturnValue(false);
        if (i == 2) cir.setReturnValue(true);
    }

    @Inject(method = "hurtServer", at = @At(value = "HEAD"), cancellable = true)
    private void warmWine(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (ModifyDamage.warmWine(living, source)) cir.setReturnValue(true);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        EndEntityTick.LIVING_EVENT.invoker().endLivingTick(living);
    }

    @ModifyVariable(method = "getDamageAfterArmorAbsorb", at = @At(value = "HEAD"), argsOnly = true)
    protected float modifyDamageBeforeArmor(float amount, DamageSource source) {
        return ModifyDamage.modify(living, source, amount);
    }

    @Inject(at = @At("TAIL"), method = "actuallyHurt")
    private void onEntityHurt(ServerLevel world, DamageSource source, float amount, CallbackInfo ci) {
        EntityHurtCallback.EVENT.invoker().hurtEntity(living, source, amount);
    }

    //翻面的生物无法发起攻击
    @Inject(method = "canAttack", at = @At(value = "HEAD"), cancellable = true)
    public void canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(ModItems.TURNOVER)) cir.setReturnValue(false);
    }
}
