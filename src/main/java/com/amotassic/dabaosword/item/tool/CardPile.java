package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.getCardPack;

public class CardPile extends TrinketItem {
    public CardPile(Properties properties) {super(properties);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.card_pile.tooltip"));
        textConsumer.accept(Component.empty());
        textConsumer.accept(Component.translatable("item.dabaosword.card_pile.tip1").withStyle(ChatFormatting.BOLD));
        textConsumer.accept(Component.translatable("item.dabaosword.card_pile.tip2", Component.keybind("key.dabaosword.select_card")).withStyle(ChatFormatting.BOLD));
        textConsumer.accept(Component.translatable("item.dabaosword.card_pile.tip3", Component.keybind("key.sprint"), Component.keybind("key.dabaosword.select_card")).withStyle(ChatFormatting.BOLD));
        textConsumer.accept(Component.translatable("item.dabaosword.card_pile.tip4", Component.keybind("key.sprint"), Component.keybind("key.sneak"), Component.keybind("key.dabaosword.select_card")).withStyle(ChatFormatting.BOLD));
    }

    @Override
    public void tick(ItemStack pile, SlotReference slot, LivingEntity entity) {
        if (entity.level() instanceof ServerLevel world && entity instanceof Player player) {
            long time = world.getGameTime();
            int skill = world.getGameRules().get(Gamerule.CHANGE_SKILL_INTERVAL) * 20;

            if (world.getGameRules().get(Gamerule.CARD_PILE_HUNGERLESS)) player.getFoodData().setFoodLevel(20);

            if (skill >= 0) {
                if (skill == 0) player.addTag("change_skill");
                else if (time % skill == 0) { //每5分钟可以切换技能
                    player.addTag("change_skill");
                    if (skill >= 600) {
                        player.sendSystemMessage(Component.translatable("dabaosword.change_skill").withStyle(ChatFormatting.BOLD));
                        player.sendSystemMessage(Component.translatable("dabaosword.change_skill2"));
                    }
                }
            }

            if (player.containerMenu.getClass() != PileScreenHandler.class && time % 20 == 0) {
                var cards = getCardPack(player);
                for (int i = 9; i < 36; i++) {
                    ItemStack item = player.getInventory().getNonEquipmentItems().get(i);
                    if (ModTools.isCard(item) && cards.isNotFull()) {
                        cards.insertStack(item.copy());
                        item.setCount(0);
                    }
                }
            }
        }
    }
}
