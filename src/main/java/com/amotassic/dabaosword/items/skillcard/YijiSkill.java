package com.amotassic.dabaosword.items.skillcard;

import com.amotassic.dabaosword.items.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class YijiSkill extends SkillItem{
    public YijiSkill(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getDamage()==0) {
            tooltip.add(Text.translatable("skill.dabaosword.enabled").formatted(Formatting.GREEN));
        } else {tooltip.add(Text.translatable("skill.dabaosword.disabled").formatted(Formatting.RED));}
        tooltip.add(Text.literal("CD: 30s"));
        tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
            if (stack.getDamage()==0) {
                player.giveItemStack(new ItemStack(ModItems.WUZHONG));
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 30,0,false,true,true));
            }
        }
    }
    //右键使用控制耐久度，从而判断是否启用
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.getDamage()==0) {stack.setDamage(1);}
        else {stack.setCount(0);user.giveItemStack(new ItemStack(SkillCards.YIJI));}
        return super.use(world, user, hand);
    }
}
