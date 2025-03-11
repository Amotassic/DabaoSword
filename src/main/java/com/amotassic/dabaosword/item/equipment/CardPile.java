package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.util.ModTools;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class CardPile extends Equipment {
    public CardPile(Settings properties) {super(properties);}

    @Override public Type getType() {return null;}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip1").formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip2", Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip3", Text.keybind("key.sneak"), Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip4", Text.keybind("key.sprint"), Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.dabaosword.card_pile.tip5", Text.keybind("key.sprint"), Text.keybind("key.sneak"), Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {return true;}

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {return ActionResult.PASS;}

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        if (reference.entity() instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld world) {
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
