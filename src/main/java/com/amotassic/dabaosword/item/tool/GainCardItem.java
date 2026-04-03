package com.amotassic.dabaosword.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.draw;

public class GainCardItem extends Item {
    public GainCardItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("item.dabaosword.gain_card.tooltip"));
    }

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!entity.level().isClientSide() && entity instanceof Player player) {
            if (!player.isCreative() && !player.isSpectator()) {
                draw(player, stack.getCount());
                stack.setCount(0);
            }
        }
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            int m;
            if (player.isShiftKeyDown()) m = player.getMainHandItem().getCount(); else m = 1;
            draw(player,m);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, player, hand);
    }
}
