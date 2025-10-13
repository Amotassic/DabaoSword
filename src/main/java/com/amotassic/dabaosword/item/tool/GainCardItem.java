package com.amotassic.dabaosword.item.tool;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.draw;

public class GainCardItem extends Item {
    public GainCardItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.dabaosword.gain_card.tooltip"));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity player) {
            if (!player.isCreative() && !player.isSpectator()) {
                draw(player, stack.getCount());
                stack.setCount(0);
            }
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && hand == Hand.MAIN_HAND) {
            int m;
            if (user.isSneaking()) m=user.getMainHandStack().getCount(); else m=1;
            draw(user,m);
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }
}
