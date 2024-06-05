package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModTools {

    @Shadow public abstract boolean isGlowing();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow public abstract boolean isDead();

    public LivingEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damagemixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

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
                //翻面的生物（除了玩家）无法造成伤害
                if (!(entity instanceof PlayerEntity) && entity.hasStatusEffect(ModItems.TURNOVER)) cir.setReturnValue(false);

                if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
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

                    if (this.isGlowing() && shouldSha(player)) {//实现铁索连环的效果，大概是好了吧
                        ItemStack stack = shaStack(player);
                        player.addCommandTag("sha");
                        if (stack.getItem() == ModItems.SHA) {
                            voice(player, Sounds.SHA);
                            if (!(entity instanceof PlayerEntity && hasTrinket(ModItems.RATTAN_ARMOR, (PlayerEntity) entity))) {
                                entity.damage(source, 5); entity.timeUntilRegen = 0;
                            }
                        }
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                        Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
                        for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isGlowing)) {
                            //处理杀的效果
                            if (stack.getItem() == ModItems.FIRE_SHA) {
                                nearbyEntity.setOnFireFor(5);
                                nearbyEntity.timeUntilRegen = 0;
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING);
                                nearbyEntity.damage(source, amount);
                            }
                            if (stack.getItem() == ModItems.THUNDER_SHA) {
                                nearbyEntity.damage(player.getDamageSources().magic(), 5);
                                nearbyEntity.timeUntilRegen = 0;
                                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                                if (lightningEntity != null) {
                                    lightningEntity.refreshPositionAfterTeleport(nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ());
                                    lightningEntity.setCosmetic(true);
                                }
                                world.spawnEntity(lightningEntity);
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING);
                                nearbyEntity.damage(source, amount);
                            }
                        } benxi(player);
                        if (!player.isCreative()) {stack.decrement(1);}
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

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        //若方天画戟被触发了，只要左键就可以造成群伤
        PlayerEntity closestPlayer = getEntityWorld().getClosestPlayer(this, 5);
        if (closestPlayer != null && hasTrinket(ModItems.FANGTIAN, closestPlayer) && !getWorld().isClient && !isDead()) {
            ItemStack stack = trinketItem(ModItems.FANGTIAN, closestPlayer);
            if (stack.get(ModItems.TAGS) != null) {
                int time = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (time > 0 && closestPlayer.handSwingTicks == 1) {
                    //给玩家本人一个极短的无敌效果，以防止被误伤
                    closestPlayer.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,2,0,false,false,false));
                    float i = (float) closestPlayer.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    this.damage(getDamageSources().playerAttack(closestPlayer), i);
                }
            }
        }

        if (livingEntity instanceof MobEntity mob && mob.hasStatusEffect(ModItems.TURNOVER)) mob.setTarget(null);
    }
    @Unique LivingEntity livingEntity = (LivingEntity) (Object) this;

    @Unique
    boolean shouldSha(PlayerEntity player) {
        return getShaSlot(player) != -1 && !player.getCommandTags().contains("sha") && !player.getCommandTags().contains("juedou") && !player.getCommandTags().contains("wanjian");
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
