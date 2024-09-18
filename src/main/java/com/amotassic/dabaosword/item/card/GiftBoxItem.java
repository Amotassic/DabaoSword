package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.LootEntry;
import com.amotassic.dabaosword.util.LootTableParser;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.give;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class GiftBoxItem extends Item {
    public GiftBoxItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.gift_box.tooltip").formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getOffHandStack();
            float chance;
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_INGOT) {
                chance = 0.01f + 0.01f * stack.getCount();
                stack.setCount(0);
                giftBox(user, chance);
            }
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_BLOCK) {
                chance = 0.01f + 0.09f * stack.getCount();
                stack.decrement(Math.min(stack.getCount(), 11));
                giftBox(user, chance);
            }
            if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
        }
        return super.use(world, user, hand);
    }

    public void giftBox(@NotNull PlayerEntity player, float chance) {
        if (new Random().nextFloat() < chance) {
            List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/draw_skill.json"));
            LootEntry selectedEntry = selectRandomEntry(lootEntries);

            ItemStack stack = new ItemStack(Registries.ITEM.get(selectedEntry.item()));
            if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
            give(player, stack);
        }
    }

    public static LootEntry selectRandomEntry(List<LootEntry> lootEntries) {
        double totalWeight = lootEntries.stream().mapToDouble(LootEntry::weight).sum();
        double randomValue = new Random().nextDouble() * totalWeight;
        double currentWeight = 0;
        for (LootEntry entry : lootEntries) {
            currentWeight += entry.weight();
            if (randomValue < currentWeight) {
                return entry;
            }
        }
        // 如果没有匹配的条目，默认返回最后一个条目
        return lootEntries.get(lootEntries.size() - 1);
    }
}
