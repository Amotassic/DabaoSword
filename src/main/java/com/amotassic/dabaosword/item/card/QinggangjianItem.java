package com.amotassic.dabaosword.item.card;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class QinggangjianItem extends SwordItem {
    public QinggangjianItem() {
        super(ToolMaterials.IRON, 1, -2.4F,new FabricItemSettings().maxDamage(400));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
        tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
    }
    //青釭剑额外伤害
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld world) {
            float extraDamage = Math.min(20, 0.25f * target.getHealth());
            target.applyDamage(world.getDamageSources().sonicBoom(attacker), extraDamage);
        }
        return super.postHit(stack, target, attacker);
    }
}