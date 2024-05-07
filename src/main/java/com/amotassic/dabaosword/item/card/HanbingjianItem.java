package com.amotassic.dabaosword.item.card;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class HanbingjianItem extends SwordItem {
    public HanbingjianItem() {
        super(ToolMaterials.IRON, 4, -2.4F,new FabricItemSettings().maxDamage(400));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getWorld().isClient) target.timeUntilRegen = 0; target.setFrozenTicks(500);
        return super.postHit(stack, target, attacker);
    }
}
