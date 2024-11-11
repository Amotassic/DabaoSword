package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CardPile extends Equipment {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip1").formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip2").formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip3").formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip4").formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip5").formatted(Formatting.BOLD));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {return true;}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (equipItem(user, stack)) return TypedActionResult.success(stack, world.isClient());
        return TypedActionResult.pass(stack);
    }

    @Override
    public void tick(ItemStack pile, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player) {
            if (player.currentScreenHandler.getClass() != PileScreenHandler.class && world.getTime() % 20 == 0) {
                CardPileInventory cards = new CardPileInventory(player);
                for (int i = 9; i < 36; i++) {
                    ItemStack item = player.getInventory().main.get(i);
                    if (ModTools.isCard(item) && cards.isNotFull()) {
                        cards.insertStack(item.copy());
                        item.setCount(0);
                    }
                }
            }
        }
    }

    @Override
    public int onDrawPhase(PlayerEntity player, ItemStack stack) {return 2;}
}
