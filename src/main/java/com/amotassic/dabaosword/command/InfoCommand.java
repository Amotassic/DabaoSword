package com.amotassic.dabaosword.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.amotassic.dabaosword.util.ModTools.openFullInv;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("info")
                .then(argument("target", EntityArgumentType.entity())
                        .executes(c -> run(EntityArgumentType.getEntity(c, "target"), c, false))
                        .then(argument("editable", BoolArgumentType.bool())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(c -> run(EntityArgumentType.getEntity(c, "target"), c, BoolArgumentType.getBool(c, "editable")))
                        )
                )
        );
    }

    private static int run(Entity entity, CommandContext<ServerCommandSource> context, boolean editable) {

        var player = context.getSource().getPlayer();
        if (player != null) {
            if (entity instanceof LivingEntity target) openFullInv(player, target, editable);
            else player.sendMessage(Text.translatable("info.fail").formatted(Formatting.RED));
        }
        return 1;
    }
}
