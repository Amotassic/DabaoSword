package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class DamageMixin extends Entity {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow protected abstract void applyDamage(DamageSource source, float amount);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract float getHealth();

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    public DamageMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Inject(method = "damage",at = @At("HEAD"), cancellable = true)
    private void damagemixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack1 = this.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack stack2 = this.getEquippedStack(EquipmentSlot.CHEST);
        boolean armor2 = stack2.getItem() == ModItems.RATTAN_CHESTPLATE;
        ItemStack stack3 = this.getEquippedStack(EquipmentSlot.LEGS);
        boolean armor3 = stack3.getItem() == ModItems.RATTAN_LEGGINGS;
        ItemStack stack4 = this.getEquippedStack(EquipmentSlot.FEET);
        boolean inrattan = armor2 || armor3;
        boolean noArmor = stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty() && stack4.isEmpty();
        Entity owner = source.getAttacker();
        if (this.hasStatusEffect(ModItems.INVULNERABLE)) {cir.setReturnValue(false);}
        //恭喜你发现了彩蛋！副手拿着幽匿催发体，然后尽情享受弹射物带来的快乐吧！
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && owner instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld serverWorld && player.getOffHandStack().getItem() == Items.SCULK_CATALYST) {
            Vec3d vec3d = player.getPos().add(0.0, 1.5f, 0.0);
            Vec3d vec3d2 = this.getEyePos().subtract(vec3d);
            Vec3d vec3d3 = vec3d2.normalize();
            for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0F, 1.0F);
            this.damage(serverWorld.getDamageSources().sonicBoom(player), 10.0f);
            double d = 0.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
            double e = 2.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
            this.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
        }
        //弹射物对藤甲无效
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan) {
            cir.setReturnValue(false);
            Objects.requireNonNull(source.getSource()).discard();
            if (owner instanceof LivingEntity entity) {
                if (armor2) {stack2.damage((int) (3 *Math.random()+1), entity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.CHEST));}
                if (armor3) {stack3.damage((int) (3 *Math.random()+1), entity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.LEGS));}
            }
        }
        //若攻击者主手没有物品，则无法击穿藤甲
        if (source.getSource() instanceof LivingEntity entity){
            if (inrattan && entity.getMainHandStack().isEmpty()) {
                cir.setReturnValue(false);
                if (armor2) {stack2.damage((int) (3 *Math.random()+1), entity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.CHEST));}
                if (armor3) {stack3.damage((int) (3 *Math.random()+1), entity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.LEGS));}
            }
            //古锭刀对没有装备的生物伤害翻倍
            if (entity.getMainHandStack().getItem() == ModItems.GUDINGDAO) {
                if (noArmor || EnchantmentHelper.getLevel(ModItems.POJUN, entity.getMainHandStack()) > 0) {
                    float health = this.getHealth();
                    if (this.getHealth()>amount) {this.setHealth(health-amount);}
                }
            }
            //被乐的生物无法造成普通攻击伤害
            if (entity.hasStatusEffect(ModItems.TOO_HAPPY)) cir.setReturnValue(false);
        }
        //被乐的生物的弹射物无法造成伤害
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && source.getAttacker() instanceof LivingEntity entity) {
            if (entity.hasStatusEffect(ModItems.TOO_HAPPY)) cir.setReturnValue(false);
        }
        //若承受火焰伤害，则 战火燃尽，嘤熊胆！
        if (source.isIn(DamageTypeTags.IS_FIRE) && inrattan) {
            if (this.isOnFire()) {
                if (this.getHealth()>0.2) {this.applyDamage(source,0.2f);}
            }
        }
    }
}
