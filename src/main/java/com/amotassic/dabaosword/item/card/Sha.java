package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.ISha;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.amotassic.dabaosword.api.event.CardEvents.hurtByCard;
import static com.amotassic.dabaosword.util.ModTools.damageSource;

public class Sha extends CardItem implements ISha {
    public Sha(Settings settings) {super(settings);}

    @Override public Type getType() {return Type.BASIC;}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.dabaosword.sha.tip").formatted(Formatting.BOLD));

        if (stack.isOf(ModItems.SHA)) {
            tooltip.add(Text.translatable("item.dabaosword.sha.tooltip"));
        }
        if (stack.isOf(ModItems.FIRE_SHA)) {
            tooltip.add(Text.translatable("item.dabaosword.fire_sha.tooltip").formatted(Formatting.RED));
        }
        if (stack.isOf(ModItems.THUNDER_SHA)) {
            tooltip.add(Text.translatable("item.dabaosword.thunder_sha.tooltip").formatted(Formatting.BLUE));
        }
    }

    @Override
    public boolean sha(LivingEntity user, LivingEntity target, float amount) {
        return target.damage((ServerWorld) user.getWorld(), user.getDamageSources().mobAttack(user), amount + 5);
    }

    @Override
    public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
        hurtByCard(target, sha);
    }

    public static class Fire extends Sha {
        public Fire(Settings settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.damage((ServerWorld) user.getWorld(), damageSource(user, DamageTypes.IN_FIRE), amount);
        }

        @Override
        public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
            target.setOnFireFor(6);
            hurtByCard(target, sha);
        }
    }

    public static class Thunder extends Sha {
        public Thunder(Settings settings) {super(settings);}

        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.damage((ServerWorld) user.getWorld(), damageSource(user, DamageTypes.LIGHTNING_BOLT), amount + 5);
        }

        @Override
        public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
            ShandianEffect.summonLightning(target, true, false);
            hurtByCard(target, sha);
        }
    }
}