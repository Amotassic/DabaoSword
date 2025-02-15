package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.pvpgame.Game;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.amotassic.dabaosword.event.PVPGameEvents.getGameManager;
import static com.amotassic.dabaosword.util.ModTools.trinketItem;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DabaoSwordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(literal("dabaosword")
                .requires(source -> source.getEntity() != null)
                .executes(c -> help(c.getSource(), 0))
                .then(argument("page", IntegerArgumentType.integer())
                        .executes(c -> help(c.getSource(), IntegerArgumentType.getInteger(c, "page")))
                )
                .then(argument("skill", ItemStackArgumentType.itemStack(access))
                        .executes(c -> execute(c, ItemStackArgumentType.getItemStackArgument(c, "skill"), 0))
                        .then(argument("value", IntegerArgumentType.integer())
                                .executes(c -> execute(c, ItemStackArgumentType.getItemStackArgument(c, "skill"), IntegerArgumentType.getInteger(c, "value")))
                        )
                )
                .then(literal("creategame").executes(DabaoSwordCommand::createGame))
                .then(literal("refusegame").executes(DabaoSwordCommand::refuseGame))
                .then(literal("discardgame").requires(source -> source.hasPermissionLevel(2))
                        .executes(c -> discardGame(c, null))
                        .then(argument("player", EntityArgumentType.player())
                               .executes(c -> discardGame(c, EntityArgumentType.getPlayer(c, "player")))
                       )
                )
               .then(literal("viewidentity").requires(source -> source.hasPermissionLevel(2))
                       .then(argument("target", EntityArgumentType.player())
                              .executes(c -> viewIdentity(c, EntityArgumentType.getPlayer(c, "target")))
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

    private static int createGame(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Game game = getGameManager().createGame(player);
        if (game == null) return 0;
        return 1;
    }

    private static int refuseGame(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) return 0;
        if (!game.isWaiting()) return 0;
        game.refuseGame(player);
        return 1;
    }

    private static int discardGame(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) player = ctx.getSource().getPlayerOrThrow();
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) {
            ServerPlayerEntity finalPlayer = player;
            ctx.getSource().sendFeedback(() -> Text.translatable("dabaosword.game.not_found", finalPlayer.getDisplayName()).formatted(Formatting.RED), false);
            return 0;
        }
        game.discardGame();
        ctx.getSource().sendFeedback(() -> Text.literal("Game discarded!"), false);
        return 1;
    }

    private static int viewIdentity(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) {
            ctx.getSource().sendFeedback(() -> Text.translatable("dabaosword.game.not_found", player.getDisplayName()).formatted(Formatting.RED), false);
            return 0;
        }
        Game.Identity identity = game.getIdentity(player);
        ctx.getSource().sendFeedback(() -> Text.translatable("dabaosword.game.view_id.tip", player.getDisplayName(), Text.translatable(identity.tag)).formatted(Game.getIdentityColor(identity)), false);
        return 1;
    }

    private static int help(ServerCommandSource source, int page) throws CommandSyntaxException {
        var player = source.getPlayerOrThrow();
        switch (page) {
            case 0 -> {
                player.sendMessage(Text.translatable("dabaosword.welcome"));
                MutableText text = Text.translatable("dabaosword.mainpage").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/dabaosword")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal("Modrinth")))).append(

                 Text.translatable("dabaosword.help.menu").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword 1")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.help.menu_hover")))));
                //System.out.println(Text.Serializer.toSortedJsonString(t1.append(t2))); //用于获取json文本
                player.sendMessage(text);
            }
            case 1 -> player.sendMessage(menu);
        }
        return 1;
    }

    private static final MutableText info = Text.translatable("dabaosword.help.info").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/info ")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.help.info_hover"))));
    private static final MutableText newGame = Text.translatable("dabaosword.newgame").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword creategame")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.newgame_hover"))));
    private static final MutableText viewId = Text.translatable("dabaosword.viewid").formatted(Formatting.LIGHT_PURPLE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dabaosword viewidentity ")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.viewid_hover"))));
    private static final MutableText disGame = Text.translatable("dabaosword.disgame").formatted(Formatting.LIGHT_PURPLE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dabaosword discardgame ")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.disgame_hover"))));
    public static final MutableText menu = info.append(newGame).append(viewId).append(disGame);
}
