package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class SkillItem extends TrinketItem implements ModTools {
    public SkillItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        if (stack.getItem() == SkillCards.LEIJI) {
            tooltip.add(Text.translatable("item.dabaosword.leiji.tooltip"));
        }

        if (stack.getItem() == SkillCards.YIJI) {
            tooltip.add(Text.literal("CD: 20s"));
            tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.JIZHI) {
            tooltip.add(Text.translatable("item.dabaosword.jizhi.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.KUROU) {
            tooltip.add(Text.translatable("item.dabaosword.kurou.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.LUOYI) {
            tooltip.add(Text.translatable("item.dabaosword.luoyi.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.TAOLUAN) {
            tooltip.add(Text.translatable("item.dabaosword.taoluan.tooltip"));
        }

        if (stack.getItem() == SkillCards.JUEQING) {
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.POJUN) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.pojun.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.KUANGGU) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(Text.translatable("item.dabaosword.kuanggu.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.LIULI) {
            tooltip.add(Text.translatable("item.dabaosword.liuli.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.MASHU || stack.getItem() == ModItems.CHITU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == SkillCards.FEIYING || stack.getItem() == ModItems.DILU) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        if (stack.getItem() == ModItems.CARD_PILE) {
            tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        }
    }
}
