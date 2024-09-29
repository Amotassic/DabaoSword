package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class GiftBoxItem extends Item {
    public GiftBoxItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.gift_box.tooltip").formatted(Formatting.GOLD));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
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

    private TypedActionResult<ItemStack> giftBox(@NotNull PlayerEntity player, float chance) {
        if (new Random().nextFloat() < chance) {
            var selectedId = parseLootTable(Identifier.of("dabaosword", "loot_tables/draw_skill.json"));
            ItemStack stack = new ItemStack(Registries.ITEM.get(selectedId));
            if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
            give(player, stack);
            if (!player.isCreative()) player.getMainHandStack().decrement(1);
            return TypedActionResult.success(player.getMainHandStack());
        }
        return TypedActionResult.pass(player.getMainHandStack());
    }
}
