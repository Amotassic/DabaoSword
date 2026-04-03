package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Random;
import java.util.function.Consumer;

public class GiftBoxItem extends Item {
    public GiftBoxItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.gift_box.tooltip").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public @NonNull InteractionResult use(Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack stack = user.getOffhandItem();
        if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND && !stack.isEmpty()) {
            float chance = 0.01f;
            if (stack.getItem() == Items.GOLD_INGOT) {
                chance += 0.01f * stack.getCount();
                stack.setCount(0);
                return giftBox(user, chance);
            }
            if (stack.getItem() == Items.GOLD_BLOCK) {
                chance += 0.09f * stack.getCount();
                stack.shrink(Math.min(stack.getCount(), 11));
                return giftBox(user, chance);
            }
        }
        return super.use(world, user, hand);
    }

    private InteractionResult giftBox(Player player, float chance) {
        if (new Random().nextFloat() < chance) {
            SkillItem.changeSkill(player);
            if (!player.isCreative()) player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
