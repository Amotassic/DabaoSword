package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.ISha;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.api.event.CardEvents.hurtByCard;
import static com.amotassic.dabaosword.util.ModTools.getDamageSource;

public class Sha extends CardItem implements ISha {
    @Override public Type getType() {return Type.BASIC;}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack, world, tooltip, tooltipContext);
        tooltip.add(Text.translatable("item.dabaosword.sha.tip").formatted(Formatting.BOLD));

        if (stack.isOf(ModItems.SHA)) tooltip.add(getTip());
        if (stack.isOf(ModItems.FIRE_SHA)) tooltip.add(getTip().formatted(Formatting.RED));
        if (stack.isOf(ModItems.THUNDER_SHA)) tooltip.add(getTip().formatted(Formatting.BLUE));
    }

    @Override
    public boolean sha(LivingEntity user, LivingEntity target, float amount) {
        return target.damage(user.getDamageSources().mobAttack(user), amount + 5);
    }

    @Override
    public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
        hurtByCard(target, sha);
    }

    public static class Fire extends Sha {
        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.damage(getDamageSource(user, DamageTypes.IN_FIRE), amount);
        }

        @Override
        public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
            target.setOnFireFor(6);
            hurtByCard(target, sha);
        }
    }

    public static class Thunder extends Sha {
        @Override
        public boolean sha(LivingEntity user, LivingEntity target, float amount) {
            return target.damage(getDamageSource(user, DamageTypes.LIGHTNING_BOLT), amount + 5);
        }

        @Override
        public void shaEffect(LivingEntity user, LivingEntity target, ItemStack sha) {
            ShandianEffect.summonLightning(target, true, false);
            hurtByCard(target, sha);
        }
    }
}