package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.event.callback.EndEntityTick;
import com.amotassic.dabaosword.event.callback.EntityHurtCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModifyDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Unique LivingEntity living = (LivingEntity) (Object) this;

    @Inject(method = "damage", at = @At("HEAD"))
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.getWorld() instanceof ServerWorld world) {
            //恭喜你发现了彩蛋！副手拿着幽匿催发体，然后尽情享受弹射物带来的快乐吧！
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && source.getAttacker() instanceof LivingEntity attacker && attacker.getOffHandStack().getItem() == Items.SCULK_CATALYST) {
                Vec3d vec3d = attacker.getPos().add(0.0, 1.5f, 0.0);
                Vec3d vec3d2 = this.getEyePos().subtract(vec3d);
                Vec3d vec3d3 = vec3d2.normalize();
                for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                    Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
                    world.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
                world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0F, 1.0F);
                this.damage(world.getDamageSources().sonicBoom(attacker), 10.0f);
                double d = 0.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                double e = 2.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                this.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void cancelDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ModifyDamage.shouldCancel(living, source, amount)) cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        EndEntityTick.LIVING_EVENT.invoker().endLivingTick((LivingEntity) (Object) this);
    }

    @Inject(method = "applyArmorToDamage", at = @At(value = "HEAD"), cancellable = true)
    protected void modifyDamageBeforeArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ModifyDamage.modify((LivingEntity) (Object) this, source, amount));
    }

    @Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
    private void onEntityHurt (final DamageSource source, float amount, CallbackInfo ci) {
        ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((LivingEntity) (Object) this, source, amount);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

    //翻面的生物无法发起攻击
    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(ModItems.TURNOVER)) cir.setReturnValue(false);
    }
}
