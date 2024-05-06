package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class DamageMixin extends Entity implements ModTools {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean isGlowing();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract void sendEquipmentBreakStatus(EquipmentSlot slot);

    public DamageMixin(EntityType<?> type, World world) {super(type, world);}

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damagemixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack2 = this.getEquippedStack(EquipmentSlot.CHEST);
        boolean armor2 = stack2.getItem() == ModItems.RATTAN_CHESTPLATE;
        ItemStack stack3 = this.getEquippedStack(EquipmentSlot.LEGS);
        boolean armor3 = stack3.getItem() == ModItems.RATTAN_LEGGINGS;
        boolean inrattan = armor2 || armor3;

        if (this.getWorld() instanceof ServerWorld world) {
            //无敌效果
            if (this.hasStatusEffect(ModItems.INVULNERABLE) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                cir.setReturnValue(false);
            }

            if (source.getSource() instanceof LivingEntity entity) {

                //决斗等物品虽然手长，但过远时普通伤害无效
                if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && this.distanceTo(entity) > 5) {
                    if (entity.getMainHandStack().getItem() == ModItems.JUEDOU || entity.getMainHandStack().getItem() == ModItems.DISCARD) cir.setReturnValue(false);
                }

                //若攻击者主手没有物品，则无法击穿藤甲
                if (inrattan && entity.getMainHandStack().isEmpty()) {
                    cir.setReturnValue(false);
                    if (armor2) {stack2.damage((int) (3 * Math.random() + 1), entity, livingEntity -> this.sendEquipmentBreakStatus(EquipmentSlot.CHEST));}
                    if (armor3) {stack3.damage((int) (3 * Math.random() + 1), entity, livingEntity -> this.sendEquipmentBreakStatus(EquipmentSlot.LEGS));}
                }

                //沈佳宜防御效果
                if (!(entity instanceof PlayerEntity) && this.hasStatusEffect(ModItems.DEFEND)) {
                    if (Objects.requireNonNull(this.getStatusEffect(ModItems.DEFEND)).getAmplifier() >= 2) {
                        cir.setReturnValue(false);
                    }
                }

                //被乐的生物无法造成普通攻击伤害
                if (entity.hasStatusEffect(ModItems.TOO_HAPPY)) cir.setReturnValue(false);

            }

            if (source.getAttacker() instanceof LivingEntity entity) {

                //弹射物对藤甲无效
                if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                    if (inrattan) {
                        cir.setReturnValue(false);
                        Objects.requireNonNull(source.getSource()).discard();
                        if (armor2) {stack2.damage((int) (3 * Math.random() + 1), entity, livingEntity -> this.sendEquipmentBreakStatus(EquipmentSlot.CHEST));}
                        if (armor3) {stack3.damage((int) (3 * Math.random() + 1), entity, livingEntity -> this.sendEquipmentBreakStatus(EquipmentSlot.LEGS));}
                    }

                    //被乐的生物的弹射物无法造成伤害
                    if (entity.hasStatusEffect(ModItems.TOO_HAPPY)) cir.setReturnValue(false);

                }

                if (entity instanceof PlayerEntity player) {

                    //恭喜你发现了彩蛋！副手拿着幽匿催发体，然后尽情享受弹射物带来的快乐吧！
                    if (source.isIn(DamageTypeTags.IS_PROJECTILE) && player.getOffHandStack().getItem() == Items.SCULK_CATALYST) {
                        Vec3d vec3d = player.getPos().add(0.0, 1.5f, 0.0);
                        Vec3d vec3d2 = this.getEyePos().subtract(vec3d);
                        Vec3d vec3d3 = vec3d2.normalize();
                        for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                            Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
                            world.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                        }
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0F, 1.0F);
                        this.damage(world.getDamageSources().sonicBoom(player), 10.0f);
                        double d = 0.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        double e = 2.5 * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        this.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                    }

                    if (this.isGlowing() && getShaSlot(player) != -1) {//实现铁索连环的效果，大概是好了吧
                        Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改3]
                        for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isGlowing)) {
                            //处理杀的效果
                            ItemStack stack = shaStack(player);
                            if (stack.getItem() == ModItems.FIRE_SHA) {
                                nearbyEntity.timeUntilRegen = 0;
                                nearbyEntity.setOnFireFor(5);
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING);
                                nearbyEntity.damage(source, amount);
                            }
                            if (stack.getItem() == ModItems.THUNDER_SHA) {
                                nearbyEntity.timeUntilRegen = 0;
                                nearbyEntity.damage(player.getDamageSources().magic(), 5);
                                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                                if (lightningEntity != null) {
                                    lightningEntity.refreshPositionAfterTeleport(nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ());
                                    lightningEntity.setCosmetic(true);
                                }
                                world.spawnEntity(lightningEntity);
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING);
                                nearbyEntity.damage(source, amount);
                            }
                        }
                    }

                    //绝情效果
                    if (hasTrinket(SkillCards.JUEQING, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                        cir.setReturnValue(false);
                        this.damage(world.getDamageSources().genericKill(), Math.min(6, amount));
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.JUEQING1, 1);} else {voice(player, Sounds.JUEQING2, 1);}
                        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 40));
                    }

                }

            }

        }
    }

    @Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
    private void onEntityHurt ( final DamageSource source, final float amount, CallbackInfo ci){
        ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((LivingEntity) (Object) this, source,
                amount);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
