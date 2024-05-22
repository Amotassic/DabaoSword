package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;

public class BenxiSkill extends SkillItem implements ModTools {
    public BenxiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.get(ModItems.TAGS) != null) {
            int benxi = Objects.requireNonNull(stack.get(ModItems.TAGS));
            tooltip.add(Text.of("奔袭：" + benxi));
        }
        tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip1").formatted(Formatting.RED));
        tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip2").formatted(Formatting.RED));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noLongHand(player)) {
            if (stack.get(ModItems.TAGS) == null) stack.set(ModItems.TAGS, 0);
            else {
                int benxi = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (benxi > 0) {
                    if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 2));
                    } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 1));
                    } else {player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi - 1));}
                }
                if (benxi == 0) {
                    if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,2));
                    } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1));
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    private boolean noLongHand(PlayerEntity player) {
        return player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }
}
