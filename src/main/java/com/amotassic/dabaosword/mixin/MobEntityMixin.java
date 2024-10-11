package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import static com.amotassic.dabaosword.util.ModTools.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Unique MobEntity mob = (MobEntity) (Object) this;

    @Inject(method = "initEquipment", at = @At(value = "TAIL"))
    protected void initEquipment(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if (!getWorld().isClient && new java.util.Random().nextFloat() < getChance()) initCards();
    }

    @Inject(method = "tryAttack", at = @At(value = "HEAD"))
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (isCard(getMainHandStack()) && target instanceof LivingEntity entity) tryUseCard(getMainHandStack(), entity);
    }

    @Unique
    private void tryUseCard(ItemStack stack, LivingEntity target) {
        if (!stack.isIn(Tags.Items.BASIC_CARD) && !stack.isOf(ModItems.WUXIE)) cardUsePre(mob, stack, target);
    }

    @Unique private float getChance() {
        Difficulty difficulty = getWorld().getDifficulty();
        if (difficulty == Difficulty.EASY) return 0.3f;
        if (difficulty == Difficulty.NORMAL) return 0.6f;
        if (difficulty == Difficulty.HARD) return 0.9f;
        return 0;
    }

    @Unique
    private void initCards() {
        if (getMainHandStack().isEmpty()) setStackInHand(Hand.MAIN_HAND, new ItemStack(getMainCard(), (int)(3 * Math.random()) + 1));
        if (getOffHandStack().isEmpty()) setStackInHand(Hand.OFF_HAND, new ItemStack(getOffCard(), (int)(2 * Math.random()) + 1));
    }

    @Unique private Item getMainCard() {
        if (new java.util.Random().nextFloat() > 0.33) {
            Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.WANJIAN, ModItems.TIESUO};
            int index = new java.util.Random().nextInt(items.length);
            return Arrays.stream(items).toList().get(index);
        }
        return ModItems.SHA;
    }

    @Unique private Item getOffCard() {
        if (new java.util.Random().nextFloat() < 0.5) return ModItems.SHAN;
        return ModItems.PEACH;
    }
}
