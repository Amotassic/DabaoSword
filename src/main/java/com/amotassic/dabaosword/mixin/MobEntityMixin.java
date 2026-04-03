package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {super(entityType, world);}

    @Unique Mob mob = (Mob) (Object) this;

    @Inject(method = "populateDefaultEquipmentSlots", at = @At(value = "TAIL"))
    protected void initEquipment(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (!level().isClientSide() && new Random().nextFloat() < getChance()) initCards();
    }

    @Inject(method = "doHurtTarget", at = @At(value = "HEAD"))
    public void tryAttack(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (isCard(getMainHandItem()) && target instanceof LivingEntity entity) tryUseCard(getMainHandItem(), entity);
    }

    @Unique
    private void tryUseCard(ItemStack stack, LivingEntity target) {
        if (!isBasic.test(stack) && !stack.is(ModItems.WUXIE)) CardItem.onUse(mob, stack, null, target);
    }

    @Unique private float getChance() {
        Difficulty difficulty = level().getDifficulty();
        if (difficulty == Difficulty.EASY) return 0.3f;
        if (difficulty == Difficulty.NORMAL) return 0.6f;
        if (difficulty == Difficulty.HARD) return 0.9f;
        return 0;
    }

    @Unique
    private void initCards() {
        if (getMainHandItem().isEmpty()) {
            setItemInHand(InteractionHand.MAIN_HAND, newCard(getMainCard()).copyWithCount((int) (3 * Math.random()) + 1));
        }
        if (getOffhandItem().isEmpty()) {
            setItemInHand(InteractionHand.OFF_HAND, newCard(getOffCard()).copyWithCount((int) (2 * Math.random()) + 1));
        }
    }

    @Unique private Item getMainCard() {
        if (new java.util.Random().nextFloat() > 0.33) {
            Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.WANJIAN, ModItems.TIESUO, ModItems.NANMAN, ModItems.JUEDOU, ModItems.SHANDIAN_ITEM};
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
