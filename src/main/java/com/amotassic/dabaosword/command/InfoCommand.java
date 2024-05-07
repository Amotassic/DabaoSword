package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.ModTools;
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

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class InfoCommand implements ModTools {
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
                if(stack.getItem() instanceof SkillItem) {
                    if (i == 0) {context.getSource().sendMessage(Text.literal(target.getEntityName()).append(Text.translatable("info.item")));}
                    context.getSource().sendMessage(stack.getName());
                    i++;
                }
            }
            if (i == 0) {context.getSource().sendMessage(Text.literal(target.getEntityName()).append(Text.translatable("info.none")));}
        } else {
            context.getSource().sendMessage(Text.literal(target.getEntityName()).append(Text.translatable("info.none")));
        }
        return 1;
    }
}
