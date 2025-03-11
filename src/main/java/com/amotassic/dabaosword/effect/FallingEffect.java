package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class FallingEffect extends StatusEffect {
    public FallingEffect() {super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);}

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        voice(entity, Sounds.FALL1,9);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.velocityModified = true; entity.setVelocity(0,-1.5,0);
        entity.fallDistance = 0;
        if (entity.isOnGround()) {
            float i = (float) entity.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)/2;
            Box box = new Box(entity.getBlockPos()).expand(3);
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != entity)) {
                nearbyEntity.damage(world, entity.getDamageSources().mobAttack(entity), i);
            }
            voice(entity, Sounds.FALL2,9);
            entity.removeStatusEffect(ModItems.FALLING_ATTACK);
        }
        return true;
    }
}
