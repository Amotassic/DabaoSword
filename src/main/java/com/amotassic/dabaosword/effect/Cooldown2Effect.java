package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static com.amotassic.dabaosword.item.equipment.ArrowRainItem.arrowRain;
import static com.amotassic.dabaosword.util.ModTools.hasTrinket;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class Cooldown2Effect extends StatusEffect {
    public Cooldown2Effect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.COOLDOWN2)).getDuration();
            //一级效果被用于万箭齐发
            if (amplifier == 1 && restTime % 5 == 0) arrowRain(entity, 3, 25);

            if (amplifier == 3 && hasTrinket(SkillCards.LEIJI, entity) && restTime >= 15) {//雷击的效果
                EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()),null);
            }
        }
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (!entity.getWorld().isClient && hasTrinket(SkillCards.LEIJI, entity) && amplifier == 3) {
            voice(entity, Sounds.LEIJI); //雷击语音播放
        }
        super.onApplied(entity, attributes, amplifier);
    }
}
