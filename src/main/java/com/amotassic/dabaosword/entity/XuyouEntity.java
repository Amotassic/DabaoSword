package com.amotassic.dabaosword.entity;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.util.ModTools.*;

public class XuyouEntity extends Monster implements RangedAttackMob {
    public XuyouEntity(EntityType<? extends Monster> entityType, Level world) {super(entityType, world);}

    private int bbcd = 0;
    private int bbTimes = 0;
    private final RangedAttackGoal bb = new RangedAttackGoal(this, 1.25,10, 7f);

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UseCardGoal(this));
        this.goalSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnType, SpawnGroupData spawnGroupData) {
        populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("bbcd", bbcd);
        output.putInt("bbTimes", bbTimes);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        bbcd = input.getInt("bbcd").orElse(0);
        bbTimes = input.getInt("bbTimes").orElse(0);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (level.getGameTime() % 200 == 0) draw(this);
        if (bbTimes >= 5) {
            goalSelector.removeGoal(bb);
            bbTimes = 0; bbcd = 150;
        }
        if (bbcd > 0) bbcd--; else goalSelector.addGoal(2, bb);
        super.customServerAiStep(level);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target.distanceTo(this) > 10) return false;
        return super.canAttack(target);
    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {return getSound("dabaosword", "xuyou");}

    @Override
    protected @NonNull SoundEvent getHurtSound(DamageSource source) {
        if (source.is(ModDT.LOSEHP)) return getSound("dabaosword", "losehp");
        return source.type().effects().sound();
    }

    @Override
    public void die(@NonNull DamageSource damageSource) {
        super.die(damageSource);
        if (level().isClientSide()) return;
        var data = d();
        for (var stack : allTrinkets(this)) {
            if(isCard(stack)) data.cards(stack, stack.getCount(), true);
        }
        CardEvents.cardDiscard(this, data);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        bbTimes++;
        target.invulnerableTime = 0;
        target.hurtServer(world(target), ModDT.bbll(this), 2);
        voice(this, "bbji");
    }
}
