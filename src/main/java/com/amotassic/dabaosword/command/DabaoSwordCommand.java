package com.amotassic.dabaosword.command;

import com.amotassic.dabaosword.api.skill.Skill;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.amotassic.dabaosword.event.PVPGameEvents.getGameManager;
import static com.amotassic.dabaosword.util.ModTools.s;
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
                .then(argument("user", EntityArgumentType.player())
                        .then(argument("skill", ItemStackArgumentType.itemStack(access))
                                .executes(c -> skill(EntityArgumentType.getPlayer(c, "user"), ItemStackArgumentType.getItemStackArgument(c, "skill"), EntityArgumentType.getPlayer(c, "user")))
                                .then(argument("target", EntityArgumentType.entity())
                                        .executes(c -> skill(EntityArgumentType.getPlayer(c, "user"), ItemStackArgumentType.getItemStackArgument(c, "skill"), (LivingEntity) EntityArgumentType.getEntity(c, "target")))
                                        .then(argument("value", IntegerArgumentType.integer())
                                                .executes(c -> skill(EntityArgumentType.getPlayer(c, "user"), ItemStackArgumentType.getItemStackArgument(c, "skill"), (LivingEntity) EntityArgumentType.getEntity(c, "target"), IntegerArgumentType.getInteger(c, "value")))
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
                .then(literal("discardgame").requires(source -> source.hasPermissionLevel(2))
                        .executes(c -> discardGame(c.getSource(), null))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(c -> discardGame(c.getSource(), EntityArgumentType.getPlayer(c, "player")))
                        )
                )
                .then(literal("viewidentity")
                        .then(argument("target", EntityArgumentType.player())
                                .executes(c -> viewIdentity(c.getSource(), EntityArgumentType.getPlayer(c, "target")))
                        )
                )
        );
    }

    private static int skill(PlayerEntity user, ItemStackArgument stack, LivingEntity target, int... value) {
        int val = value.length == 0 ? 0 : value[0];
        ItemStack skill = trinketItem(stack.getItem(), user);
        if (skill.getItem() instanceof CSkill s) s.triggerSkill(user, s(skill), target, val);
        return 1;
    }

    public interface CSkill {
        void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value);
    }

    private static int createGame(ServerCommandSource ctx, int type) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getPlayerOrThrow();
        Game game = getGameManager().createGame(player, type);
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

    private static int discardGame(ServerCommandSource ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) player = ctx.getPlayerOrThrow();
        Game game = getGameManager().getGameByPlayer(player);
        if (game == null) {
            ctx.sendMessage(Text.translatable("dabaosword.game.not_found", player.getDisplayName()).formatted(Formatting.RED));
            return 0;
        }
        game.discardGame();
        ctx.sendFeedback(() -> Text.literal("Game discarded!"), false);
        return 1;
    }

    private static int viewIdentity(ServerCommandSource source, ServerPlayerEntity target) throws CommandSyntaxException {
        var player = source.getPlayerOrThrow();
        Game game = getGameManager().getGameByPlayer(target);
        if (game == null) {
            source.sendMessage(Text.translatable("dabaosword.game.not_found", target.getDisplayName()).formatted(Formatting.RED));
            return 0;
        }
        Game.Identity id = game.getIdentity(target);
        if (player == target) {
            feedbackIdentity(source, target, id);
            return 1;
        } else {
            if (player.hasPermissionLevel(2)) {
                feedbackIdentity(source, target, id);
                return 1;
            } else {
                source.sendMessage(Text.translatable("dabaosword.game.view_id.fail").formatted(Formatting.RED));
                return 0;
            }
        }
    }
    private static void feedbackIdentity(ServerCommandSource source, ServerPlayerEntity target, Game.Identity id) {
        source.sendFeedback(() -> Text.translatable("dabaosword.game.view_id.tip", target.getDisplayName(), Text.translatable(id.tag)).formatted(Game.getIdentityColor(id), Formatting.BOLD), false);
    }

    private static int help(ServerCommandSource source, int page) throws CommandSyntaxException {
        var player = source.getPlayerOrThrow();
        switch (page) {
            case 0 -> {
                player.sendMessage(Text.translatable("dabaosword.welcome"));
                MutableText text = Text.translatable("dabaosword.mainpage").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/dabaosword")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Modrinth")))).append(

                Text.translatable("dabaosword.help.menu").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword 1")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.help.menu_hover")))));
                //System.out.println(Text.Serializer.toSortedJsonString(t1.append(t2))); //用于获取json文本
                player.sendMessage(text);
            }
            case 1 -> player.sendMessage(menu);
            case 2 -> {
                MutableText text = Text.translatable("dabaosword.rule").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword 3"))).append(

                Text.translatable("dabaosword.newgame0").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword creategame 0")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.newgame0_hover"))))).append(

                Text.translatable("dabaosword.newgame1").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword creategame 1")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.newgame1_hover"))))
                );
                player.sendMessage(text);
            }
            case 3 -> {
                player.sendMessage(Text.translatable("dabaosword.rule1"));
                player.sendMessage(Text.translatable("dabaosword.rule2"));
                player.sendMessage(Text.translatable("dabaosword.rule3"));
                player.sendMessage(Text.translatable("dabaosword.rule4"));
                player.sendMessage(Text.translatable("dabaosword.rule5"));
            }
        }
        return 1;
    }

    private static final MutableText info = Text.translatable("dabaosword.help.info").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/info ")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.help.info_hover")))),
    newGame = Text.translatable("dabaosword.newgame").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword 2")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.newgame_hover")))),
    viewId = Text.translatable("dabaosword.viewid").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dabaosword viewidentity @s")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.viewid_hover")))),
    disGame = Text.translatable("dabaosword.disgame").formatted(Formatting.LIGHT_PURPLE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dabaosword discardgame ")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("dabaosword.disgame_hover"))));
    public static final MutableText menu = info.append(newGame).append(viewId).append(disGame);
}
