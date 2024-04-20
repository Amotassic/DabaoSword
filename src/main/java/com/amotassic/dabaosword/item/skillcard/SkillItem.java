package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class SkillItem extends Item implements ModTools {
    public SkillItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getItem() == SkillCards.JIZHI) {
            tooltip.add(Text.translatable("item.dabaosword.jizhi.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.JUEQING) {
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LIULI) {
            tooltip.add(Text.translatable("item.dabaosword.liuli.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.MASHU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == SkillCards.FEIYING) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (hasItem(player, SkillCards.MASHU)) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1));
            }
            if (hasItem(player, SkillCards.FEIYING)) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENSE, 10,1));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
