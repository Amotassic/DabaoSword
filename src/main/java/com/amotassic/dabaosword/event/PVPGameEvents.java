package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.PVPGameTickCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.pvpgame.Game;
import com.amotassic.dabaosword.pvpgame.GameManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jspecify.annotations.NonNull;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PVPGameEvents implements ServerLevelEvents.Load, ServerTickEvents.StartTick, ServerTickEvents.StartLevelTick, PVPGameTickCallback {
    private static GameManager gameManager;

    public static GameManager getGameManager() {return gameManager;}

    @Override
    public void onLevelLoad(@NonNull MinecraftServer minecraftServer, ServerLevel world) {
        //只需要保存在主世界的data目录下即可
        if (world.dimension() == Level.OVERWORLD) gameManager = world.getDataStorage().computeIfAbsent(GameManager.getPersistentStateType());
    }

    @Override
    public void onStartTick(ServerLevel world) {
        //防止每个维度都加载一次，暂时不知道用什么更优雅的办法
        if (world.dimension() == Level.OVERWORLD) gameManager.tick(world);
    }

    @Override
    public void onGameTick(Game game, ServerLevel world) {
        int countDown = game.getCountDown();

        if (game.isWaiting()) countDownTip(game, countDown);
        if (countDown == 0) onGameStart(game, world);
        if (game.isOn()) handleTimeOut(game, game.getTimeOut());

        if (game.neiLives <= 0) { //内奸和另一个队伍已淘汰，谁活着谁就胜利
            if (game.fanLives <= 0) game.win(Game.Identity.ZHONG);
            if (game.zhongLives <= 0) game.win(Game.Identity.FAN);
        } else {
            if (game.fanLives <= 0) {
                twoTeam(game, Game.Identity.ZHONG);
                if (game.zhongLives <= 0) game.win(Game.Identity.NEI); //忠臣反贼都淘汰，内奸胜利（无人得分才可能会这样）
            }
            if (game.zhongLives <= 0) twoTeam(game, Game.Identity.FAN);
        }
    }

    public static void onGameCreate(ServerPlayer player, Game game, Set<UUID> players) {
        MutableComponent text = Component.translatable("dabaosword.game.create", player.getDisplayName(), players.size());
        if (game.getPrimaryData().get("neiCount") == 0) text.append(Component.translatable("dabaosword.game.no_turn_coat"));
        game.forEachPlayer(p -> p.sendSystemMessage(text));
    }

    private void countDownTip(Game game, int countDown) {
        if (countDown % 20 != 0) return;
        game.forEachPlayer(player -> {
            if (countDown % 100 == 0) {
                Component quit = Component.translatable("dabaosword.refuse").withStyle(ChatFormatting.RED).withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword refusegame")).withHoverEvent(new HoverEvent.ShowText(Component.translatable("dabaosword.refuse_hover"))));
                player.sendSystemMessage(Component.translatable("dabaosword.game.wait", countDown / 20).append(quit));
                return;
            }
            Set<Integer> times = Set.of(60, 40, 20);
            if (times.contains(countDown)) {
                voice(player, SoundEvents.NOTE_BLOCK_BELL.value());
                title(player, Component.literal(String.valueOf(countDown / 20)));
            }
        });
    }

    private void onGameStart(Game game, ServerLevel world) {
        //添加死亡计分板
        var scoreboard = world.getServer().getScoreboard();
        var obj = scoreboard.getObjectives().stream().filter(o -> o.getName().equals("dabaosword.death")).findFirst().orElse(null);
        var criterion = ObjectiveCriteria.DEATH_COUNT;
        if (obj == null) obj = scoreboard.addObjective("dabaosword.death", criterion, Component.translatable("dabaosword.score.death"), criterion.getDefaultRenderType(), false, null);
        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, obj);

        game.forEachPlayer(player -> {
            var identity = game.getIdentity(player);
            ChatFormatting color = Game.getIdentityColor(identity);
            var o1 = Component.translatable(identity.tag); var o2 = Component.translatable(identity.tag + ".tip");
            var o3 = Component.translatable("dabaosword.game.start.tip", o1, o2).withStyle(color);
            title(player, Component.translatable("dabaosword.game.start").withStyle(ChatFormatting.GOLD));
            subtitle(player, o3); player.sendSystemMessage(o3);
            voice(player, SoundEvents.RAID_HORN.value(), 32);
        });
    }

    private void handleTimeOut(Game game, int timeOut) {
        if (game.getGameTime() % 20 != 0) return;
        if (timeOut == 60 || timeOut == 30) game.forEachPlayer(player -> player.sendSystemMessage(Component.translatable("dabaosword.game.timeout.warn", timeOut).withStyle(ChatFormatting.YELLOW)));
        if (timeOut == 0) game.timeOut();
    }

    /**当反贼或忠臣被淘汰后，仅剩下内奸和另外一队，判定哪队胜利
     * @param identity 除内奸外存活的队伍*/
    private void twoTeam(Game game, Game.Identity identity) {
        var nei = Game.Identity.NEI; if (identity == nei) return; //防呆设计
        if (game.getScore(identity) > game.neiScore) game.win(identity);
        if (game.getScore(identity) < game.neiScore) game.win(nei);
    }

    public static final Map<ServerPlayer, CardPileInventory> PLAYER_CARD_PACKS = new HashMap<>();

    public void onStartTick(MinecraftServer server) {
        DabaoSword.server = server;
        List<ServerPlayer> playerList = server.getPlayerList().getPlayers();
        for (var player : playerList) {
            if (!PLAYER_CARD_PACKS.containsKey(player) && hasTrinket(ModItems.CARD_PILE, player)) PLAYER_CARD_PACKS.put(player, new CardPileInventory(player));
        }
        PLAYER_CARD_PACKS.keySet().removeIf(player -> player == null || player.isRemoved() || !hasTrinket(ModItems.CARD_PILE, player));
    }
}
