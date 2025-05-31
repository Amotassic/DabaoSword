package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.getCardPack;

public class CardPile extends TrinketItem {
    public CardPile(Settings properties) {super(properties);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.dabaosword.card_pile.tooltip"));
        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.dabaosword.card_pile.tip1").formatted(Formatting.BOLD));
        textConsumer.accept(Text.translatable("item.dabaosword.card_pile.tip2", Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
        textConsumer.accept(Text.translatable("item.dabaosword.card_pile.tip3", Text.keybind("key.sprint"), Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
        textConsumer.accept(Text.translatable("item.dabaosword.card_pile.tip4", Text.keybind("key.sprint"), Text.keybind("key.sneak"), Text.keybind("key.dabaosword.select_card")).formatted(Formatting.BOLD));
    }

    @Override
    public void tick(ItemStack pile, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player) {
            long time = world.getTime();
            int skill = world.getGameRules().getInt(Gamerule.CHANGE_SKILL_INTERVAL) * 20;

            if (world.getGameRules().getBoolean(Gamerule.CARD_PILE_HUNGERLESS)) player.getHungerManager().setFoodLevel(20);

            if (skill >= 0) {
                if (skill == 0) player.addCommandTag("change_skill");
                else if (time % skill == 0) { //每5分钟可以切换技能
                    player.addCommandTag("change_skill");
                    if (skill >= 600) {
                        player.sendMessage(Text.translatable("dabaosword.change_skill").formatted(Formatting.BOLD), false);
                        player.sendMessage(Text.translatable("dabaosword.change_skill2"), false);
                    }
                }
            }

            if (player.currentScreenHandler.getClass() != PileScreenHandler.class && time % 20 == 0) {
                var cards = getCardPack(player);
                for (int i = 9; i < 36; i++) {
                    ItemStack item = player.getInventory().getMainStacks().get(i);
                    if (ModTools.isCard(item) && cards.isNotFull()) {
                        cards.insertStack(item.copy());
                        item.setCount(0);
                    }
                }
            }
        }
    }
}
