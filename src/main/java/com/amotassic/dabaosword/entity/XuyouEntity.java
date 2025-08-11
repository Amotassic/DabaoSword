package com.amotassic.dabaosword.entity;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.*;

public class XuyouEntity extends HostileEntity implements RangedAttackMob {
    public XuyouEntity(EntityType<? extends HostileEntity> entityType, World world) {super(entityType, world);}

    private int bbcd = 0;
    private int bbTimes = 0;
    private final ProjectileAttackGoal bb = new ProjectileAttackGoal(this, 1.25,10, 7f);

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new UseCardGoal(this));
        this.goalSelector.add(2, new RevengeGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.ATTACK_SPEED, 1.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        initEquipment(world.getRandom(), difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("bbcd", bbcd);
        view.putInt("bbTimes", bbTimes);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        bbcd = view.getInt("bbcd", 0);
        bbTimes = view.getInt("bbTimes", 0);
    }

    @Override
    public void tickMovement() {
        if (getWorld().getTime() % 200 == 0) draw(this);
        if (bbTimes >= 5) {
            goalSelector.remove(bb);
            bbTimes = 0; bbcd = 150;
        }
        if (bbcd > 0) bbcd--; else goalSelector.add(2, bb);
        super.tickMovement();
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target.distanceTo(this) > 10) return false;
        return super.canTarget(target);
    }

    @Override
    protected SoundEvent getDeathSound() {return getSound("dabaosword", "xuyou");}

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (source.isOf(ModDT.LOSEHP)) return getSound("dabaosword", "losehp");
        return source.getType().effects().getSound();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (getWorld().isClient) return;
        var data = d();
        for (var stack : allTrinkets(this)) {
            if(isCard(stack)) data.cards(stack, stack.getCount(), true);
        }
        CardEvents.cardDiscard(this, data);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        bbTimes++;
        target.timeUntilRegen = 0;
        target.damage(world(target), ModDT.bbll(this), 2);
        voice(this, "bbji");
    }
}
