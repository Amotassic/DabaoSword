package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemMixin extends GolemEntity implements RangedAttackMob {
    public SnowGolemMixin(EntityType<? extends GolemEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "initGoals", at = @At(value = "TAIL"))
    public void initGoals(CallbackInfo ci) {
        this.goalSelector.getGoals().removeIf(goal -> goal.getGoal() instanceof ProjectileAttackGoal);
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 1, 20.0f));
    }
}
