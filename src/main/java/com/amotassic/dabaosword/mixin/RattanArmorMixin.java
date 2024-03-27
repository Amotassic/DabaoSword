package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class RattanArmorMixin extends Entity {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow protected abstract void applyDamage(DamageSource source, float amount);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract float getHealth();

    public RattanArmorMixin(EntityType<?> type, World world) {
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
        //弹射物对藤甲无效
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan) {
            cir.setReturnValue(false);
            Objects.requireNonNull(source.getSource()).discard();
            Entity owner = source.getAttacker();
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
