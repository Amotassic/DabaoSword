package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class FallingEffect extends MobEffect {
    public FallingEffect() {super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);}

    @Override
    public void onEffectAdded(@NonNull LivingEntity entity, int amplifier) {
        voice(entity, "falling_attack1",9);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, LivingEntity entity, int amplifier) {
        entity.hurtMarked = true; entity.setDeltaMovement(0,-1.5,0);
        entity.fallDistance = 0;
        if (entity.onGround()) {
            float i = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE)/2;
            AABB box = new AABB(entity.getOnPos()).inflate(3);
            for (LivingEntity nearbyEntity : world.getEntitiesOfClass(LivingEntity.class, box, LivingEntity -> LivingEntity != entity)) {
                nearbyEntity.hurtServer(world, entity.damageSources().mobAttack(entity), i);
            }
            voice(entity, "falling_attack2",9);
            entity.removeEffect(ModItems.FALLING_ATTACK);
        }
        return true;
    }
}
