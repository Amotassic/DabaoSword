package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class YijiSkill extends SkillItem implements ModTools {
    public YijiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getDamage() == 0) {
            tooltip.add(Text.translatable("skill.dabaosword.enabled").formatted(Formatting.GREEN));
        } else {tooltip.add(Text.translatable("skill.dabaosword.disabled").formatted(Formatting.RED));}
        tooltip.add(Text.literal("CD: 20s"));
        tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
    }

    //右键使用控制是否启用
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.getDamage() == 0) {stack.setDamage(1);}
            else {stack.setDamage(0);}
        }
        return super.use(world, user, hand);
    }
}
