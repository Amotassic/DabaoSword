package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.voice;
import static com.amotassic.dabaosword.util.ModTools.world;

public class BBjiItem extends Item {
    public BBjiItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.bbji.tooltip"));
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide()) {
            AABB box = new AABB(user.getOnPos()).inflate(13);
            for (LivingEntity nearbyEntity : world.getEntitiesOfClass(LivingEntity.class, box, LivingEntity -> LivingEntity != user)) {
                nearbyEntity.invulnerableTime = 0;
                nearbyEntity.hurtServer(world(user), ModDT.bbll(user),2);
            }
            voice(user, this);
            ItemStack stack = user.getItemInHand(hand);
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, user, slot);
        }
        return super.use(world, user, hand);
    }
}
