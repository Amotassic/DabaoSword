package com.amotassic.dabaosword.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public class LetMeCCItem extends Item {
    public LetMeCCItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.let_me_cc.tooltip"));
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack itemStack, Player user, @NonNull LivingEntity target, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            voice(user, this, 1);
            openFullInv(user, target, true);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(itemStack, user, target, hand);
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (!user.isShiftKeyDown()) {
                LivingEntity closest = getClosestEntity(user, LivingEntity.class, 10, LivingEntity::isAlive);
                if (closest != null) {
                    voice(user, this, 1);
                    openFullInv(user, closest, true);
                    return InteractionResult.SUCCESS_SERVER;
                }
            } else {
                voice(user, this, 1);
                openFullInv(user, user, true);
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.use(world, user, hand);
    }
}
