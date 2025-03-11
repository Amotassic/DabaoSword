package com.amotassic.dabaosword.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

import static com.amotassic.dabaosword.util.ModTools.trinketItem;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DabaoSwordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(literal("dabaosword")
                .requires(source -> source.getEntity() != null)
                .then(argument("skill", ItemStackArgumentType.itemStack(access))
                        .executes(c -> execute(c, ItemStackArgumentType.getItemStackArgument(c, "skill"), 0))
                        .then(argument("value", IntegerArgumentType.integer())
                                .executes(c -> execute(c, ItemStackArgumentType.getItemStackArgument(c, "skill"), IntegerArgumentType.getInteger(c, "value")))
                        )
                )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, ItemStackArgument stack, int value) {
        LivingEntity entity = (LivingEntity) ctx.getSource().getEntity();
        ItemStack skill = trinketItem(stack.getItem(), entity);
        if (skill.getItem() instanceof CSkill s) s.triggerSkill(entity, skill, value);
        return 1;
    }

    public interface CSkill {
        default void triggerSkill(LivingEntity entity, ItemStack stack, int value) {}
    }
}
