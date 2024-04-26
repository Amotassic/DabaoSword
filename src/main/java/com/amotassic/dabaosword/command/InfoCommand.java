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

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

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
            List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
            for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                ItemStack stack = entry.getRight();
                if(stack.getItem() instanceof SkillItem || stack.getItem() instanceof Defense|| stack.getItem() instanceof Reach) {
                    context.getSource().sendMessage(stack.getName());
                }
            }
        } else {
            context.getSource().sendMessage(Text.literal(target.getEntityName()).append("没有任何技能"));
        }
        return 1;
    }
}
