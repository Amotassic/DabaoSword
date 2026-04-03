package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.pvpgame.Game;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.net.URI;

import static com.amotassic.dabaosword.event.PVPGameEvents.getGameManager;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DabaoSwordCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access, Commands.CommandSelection selection) {
        dispatcher.register(literal("dabaosword")
                .requires(source -> source.getEntity() != null)
                .executes(c -> help(c.getSource(), 0))
                .then(argument("page", IntegerArgumentType.integer())
                        .executes(c -> help(c.getSource(), IntegerArgumentType.getInteger(c, "page")))
                )
                .then(argument("user", EntityArgument.player())
                        .then(argument("skill", ItemArgument.item(access))
                                .executes(c -> skill(EntityArgument.getPlayer(c, "user"), ItemArgument.getItem(c, "skill"), EntityArgument.getPlayer(c, "user")))
                                .then(argument("target", EntityArgument.entity())
                                        .executes(c -> skill(EntityArgument.getPlayer(c, "user"), ItemArgument.getItem(c, "skill"), (LivingEntity) EntityArgument.getEntity(c, "target")))
                                        .then(argument("value", IntegerArgumentType.integer())
                                                .executes(c -> skill(EntityArgument.getPlayer(c, "user"), ItemArgument.getItem(c, "skill"), (LivingEntity) EntityArgument.getEntity(c, "target"), IntegerArgumentType.getInteger(c, "value")))
                                        )
                                )
                        )
                )
                .then(literal("creategame")
                        .then(argument("type", IntegerArgumentType.integer())
                                .executes(c -> createGame(c.getSource(), IntegerArgumentType.getInteger(c, "type")))
                        )
                )
                .then(literal("refusegame").executes(DabaoSwordCommand::refuseGame))
                .then(literal("discardgame").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(c -> discardGame(c.getSource(), null))
                        .then(argument("player", EntityArgument.player())
                                .executes(c -> discardGame(c.getSource(), EntityArgument.getPlayer(c, "player")))
                        )
                )
                .then(literal("viewidentity")
                        .then(argument("target", EntityArgument.player())
                                .executes(c -> viewIdentity(c.getSource(), EntityArgument.getPlayer(c, "target")))
                        )
                )
                .then(literal("info")
                        .then(argument("target", EntityArgument.entity())
                                .executes(c -> info(c, EntityArgument.getEntity(c, "target"), false))
                                .then(argument("editable", BoolArgumentType.bool())
                                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                        .executes(c -> info(c, EntityArgument.getEntity(c, "target"), BoolArgumentType.getBool(c, "editable")))
                                )
                        )
                )
        );
    }

    private static int info(CommandContext<CommandSourceStack> context, Entity entity, boolean editable) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        if (entity instanceof LivingEntity target) {
            openFullInv(player, target, editable);
            return 1;
        } else {
            player.sendSystemMessage(Component.translatable("info.fail").withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static int skill(Player user, ItemInput stack, LivingEntity target, int... value) {
        int val = value.length == 0 ? 0 : value[0];
        ItemStack skill = trinketItem(stack.item().value(), user);
        if (skill.getItem() instanceof CSkill s) s.triggerSkill(user, s(skill), target, val);
        return 1;
    }

    public interface CSkill {
        void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value);
    }

    private static int createGame(CommandSourceStack ctx, int type) throws CommandSyntaxException {
        ServerPlayer player = ctx.getPlayerOrException();
        Game game = getGameManager().createGame(player, type);
        if (game == null) return 0;
        return 1;
    }

    private static int refuseGame(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) return 0;
        if (!game.isWaiting()) return 0;
        game.refuseGame(player);
        return 1;
    }

    private static int discardGame(CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        if (player == null) player = ctx.getPlayerOrException();
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) {
            ctx.sendFailure(Component.translatable("dabaosword.game.not_found", player.getDisplayName()).withStyle(ChatFormatting.RED));
            return 0;
        }
        game.discardGame();
        ctx.sendSuccess(() -> Component.literal("Game discarded!"), false);
        return 1;
    }

    private static int viewIdentity(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        var player = source.getPlayerOrException();
        Game game = getGameManager().getGameByPlayer(target);
        if (game == null) {
            source.sendFailure(Component.translatable("dabaosword.game.not_found", target.getDisplayName()).withStyle(ChatFormatting.RED));
            return 0;
        }
        Game.Identity id = game.getIdentity(target);
        if (player == target) {
            feedbackIdentity(source, target, id);
            return 1;
        } else {
            if (player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS))) {
                feedbackIdentity(source, target, id);
                return 1;
            } else {
                source.sendFailure(Component.translatable("dabaosword.game.view_id.fail").withStyle(ChatFormatting.RED));
                return 0;
            }
        }
    }
    private static void feedbackIdentity(CommandSourceStack source, ServerPlayer target, Game.Identity id) {
        source.sendSuccess(() -> Component.translatable("dabaosword.game.view_id.tip", target.getDisplayName(), Component.translatable(id.tag)).withStyle(Game.getIdentityColor(id), ChatFormatting.BOLD), false);
    }

    private static int help(CommandSourceStack source, int page) throws CommandSyntaxException {
        var player = source.getPlayerOrException();
        switch (page) {
            case 0 -> {
                player.sendSystemMessage(Component.translatable("dabaosword.welcome"));
                MutableComponent text = Component.translatable("dabaosword.mainpage").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/dabaosword"))).withHoverEvent(new HoverEvent.ShowText(Component.literal("Modrinth")))).append(

                Component.translatable("dabaosword.help.menu").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword 1")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.help.menu_hover")))));
                //System.out.println(Text.Serialization.toJsonString(text, DynamicRegistryManager.EMPTY)); //用于获取json文本
                player.sendSystemMessage(text);
            }
            case 1 -> player.sendSystemMessage(menu);
            case 2 -> {
                MutableComponent text = Component.translatable("dabaosword.rule").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword 3"))).append(

                Component.translatable("dabaosword.newgame0").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword creategame 0")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.newgame0_hover"))))).append(

                Component.translatable("dabaosword.newgame1").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword creategame 1")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.newgame1_hover"))))
                );
                player.sendSystemMessage(text);
            }
            case 3 -> {
                player.sendSystemMessage(Component.translatable("dabaosword.rule1"));
                player.sendSystemMessage(Component.translatable("dabaosword.rule2"));
                player.sendSystemMessage(Component.translatable("dabaosword.rule3"));
                player.sendSystemMessage(Component.translatable("dabaosword.rule4"));
                player.sendSystemMessage(Component.translatable("dabaosword.rule5"));
            }
        }
        return 1;
    }

    private static final MutableComponent info = Component.translatable("dabaosword.help.info").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/dabaosword info ")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.help.info_hover")))),
    newGame = Component.translatable("dabaosword.newgame").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword 2")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.newgame_hover")))),
    viewId = Component.translatable("dabaosword.viewid").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/dabaosword viewidentity @s")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.viewid_hover")))),
    disGame = Component.translatable("dabaosword.disgame").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/dabaosword discardgame ")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.disgame_hover"))));
    public static final MutableComponent menu = info.append(newGame).append(viewId).append(disGame);
}
