package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.Random;

import static com.amotassic.dabaosword.item.equipment.ArrowRainItem.arrowRain;
import static com.amotassic.dabaosword.util.ModTools.*;

public class Cooldown2Effect extends StatusEffect {
    public Cooldown2Effect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld world) {
            int restTime = Objects.requireNonNull(entity.getStatusEffect(ModItems.COOLDOWN2)).getDuration();
            //一级效果被用于万箭齐发
            if (amplifier == 1) {
                arrowRain(player, 3);
                if (restTime <= 1) player.getCommandTags().remove("wanjian");
            }

            if (amplifier == 3 && hasTrinket(SkillCards.LEIJI, player) && restTime >= 15) {//雷击的效果
                EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ()),null);
            }
        }
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity player && !player.getWorld().isClient && hasTrinket(SkillCards.LEIJI, player) && amplifier == 3) {
            //雷击语音播放
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.LEIJI1);} else {voice(player, Sounds.LEIJI2);}
        }
        super.onApplied(entity, attributes, amplifier);
    }
}
