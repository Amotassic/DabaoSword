package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.item.horse.Defense;
import com.amotassic.dabaosword.item.horse.Reach;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("info")
                .then(argument("target", EntityArgumentType.player())
                .executes(c -> run(EntityArgumentType.getPlayer(c, "target"), c))
                ));
    }

    private static int run(PlayerEntity target, CommandContext<ServerCommandSource> context) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);

        if(component.isPresent()) {
            int i = 0;
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                ItemStack stack = entry.getRight();
                if(stack.getItem() instanceof SkillItem || stack.getItem() instanceof Defense|| stack.getItem() instanceof Reach) {
                    if (i == 0) {context.getSource().sendMessage(Text.literal(target.getNameForScoreboard()).append("装备了以下物品："));}
                    context.getSource().sendMessage(stack.getName());
                    i++;
                }
            }
            if (i == 0) {context.getSource().sendMessage(Text.literal(target.getNameForScoreboard()).append("没有装备任何技能或马匹"));}
        } else {
            context.getSource().sendMessage(Text.literal(target.getNameForScoreboard()).append("没有装备任何技能或马匹"));
        }
        return 1;
    }
}
