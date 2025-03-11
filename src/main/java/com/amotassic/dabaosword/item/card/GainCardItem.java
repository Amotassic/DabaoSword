package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.draw;

public class GainCardItem extends Item {
    public GainCardItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.gain_card.tooltip"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            if (!player.isCreative() && !player.isSpectator()) {
                draw(player, stack.getCount());
                stack.setCount(0);
            }
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int m;
            if (user.isSneaking()) m=user.getMainHandStack().getCount(); else m=1;
            draw(user,m);
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }
}
