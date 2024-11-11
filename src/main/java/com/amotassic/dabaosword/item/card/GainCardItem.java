package com.amotassic.dabaosword.item.card;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.draw;

public class GainCardItem extends Item {
    public GainCardItem() {super(new Settings());}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int m;
            if (user.isSneaking()) m=user.getMainHandStack().getCount(); else m=1;
            draw(user,m);
            return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }
}
