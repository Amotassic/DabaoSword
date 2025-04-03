package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class GiftBoxItem extends Item {
    public GiftBoxItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.gift_box.tooltip").formatted(Formatting.GOLD));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getOffHandStack();
        if (!world.isClient && hand == Hand.MAIN_HAND && !stack.isEmpty()) {
            float chance = 0.01f;
            if (stack.getItem() == Items.GOLD_INGOT) {
                chance += 0.01f * stack.getCount();
                stack.setCount(0);
                return giftBox(user, chance);
            }
            if (stack.getItem() == Items.GOLD_BLOCK) {
                chance += 0.09f * stack.getCount();
                stack.decrement(Math.min(stack.getCount(), 11));
                return giftBox(user, chance);
            }
        }
        return super.use(world, user, hand);
    }

    private ActionResult giftBox(@NotNull PlayerEntity player, float chance) {
        if (new Random().nextFloat() < chance) {
            SkillItem.changeSkill(player);
            if (!player.isCreative()) player.getMainHandStack().decrement(1);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }
}
