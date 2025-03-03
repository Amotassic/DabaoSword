package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.PVPGameTickCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.pvpgame.Game;
import com.amotassic.dabaosword.pvpgame.GameManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PVPGameEvents implements ServerWorldEvents.Load, ServerTickEvents.StartTick, ServerTickEvents.StartWorldTick, PVPGameTickCallback {
    private static GameManager gameManager;

    public static GameManager getGameManager() {return gameManager;}

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        //只需要保存在主世界的data目录下即可
        if (world.getRegistryKey() == World.OVERWORLD) gameManager = world.getPersistentStateManager().getOrCreate(nbt -> GameManager.fromNbt(world, nbt), () -> new GameManager(world), "dabaosword_game");
    }

    @Override
    public void onStartTick(ServerWorld world) {
        //防止每个维度都加载一次，暂时不知道用什么更优雅的办法
        if (world.getRegistryKey() == World.OVERWORLD) gameManager.tick();
    }

    @Override
    public void onGameTick(Game game, ServerWorld world) {
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

    public static void onGameCreate(ServerPlayerEntity player, Game game, Set<UUID> players) {
        game.forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.game.create", player.getDisplayName(), players.size())));
    }

    private void countDownTip(Game game, int countDown) {
        if (countDown % 20 != 0) return;
        game.forEachPlayer(player -> {
            if (countDown % 100 == 0) {
                Text quit = Text.translatable("dabaosword.refuse").formatted(Formatting.RED).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword refusegame")).withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.translatable("dabaosword.refuse_hover"))));
                player.sendMessage(Text.translatable("dabaosword.game.wait", countDown / 20).append(quit));
                return;
            }
            Set<Integer> times = Set.of(60, 40, 20);
            if (times.contains(countDown)) {
                voice(player, SoundEvents.BLOCK_NOTE_BLOCK_BELL.value());
                title(player, Text.literal(String.valueOf(countDown / 20)));
            }
        });
    }

    private void onGameStart(Game game, ServerWorld world) {
        //添加死亡计分板
        var scoreboard = world.getServer().getScoreboard();
        var obj = scoreboard.getObjective("dabaosword.death");
        var criterion = ScoreboardCriterion.DEATH_COUNT;
        if (obj == null) obj = scoreboard.addObjective("dabaosword.death", criterion, Text.translatable("dabaosword.score.death"), criterion.getDefaultRenderType());
        scoreboard.setObjectiveSlot(1, obj);

        game.forEachPlayer(player -> {
            var identity = game.getIdentity(player);
            Formatting color = Game.getIdentityColor(identity);
            Text o1 = Text.translatable(identity.tag); Text o2 = Text.translatable(identity.tag + ".tip");
            Text o3 = Text.translatable("dabaosword.game.start.tip", o1, o2).formatted(color);
            title(player, Text.translatable("dabaosword.game.start").formatted(Formatting.GOLD));
            subtitle(player, o3); player.sendMessage(o3);
            voice(player, SoundEvents.EVENT_RAID_HORN.value(), 32);
        });
    }

    private void handleTimeOut(Game game, int timeOut) {
        if (game.getGameTime() % 20 != 0) return;
        if (timeOut == 60 || timeOut == 30) game.forEachPlayer(player -> player.sendMessage(Text.translatable("dabaosword.game.timeout.warn", timeOut).formatted(Formatting.YELLOW)));
        if (timeOut == 0) game.timeOut();
    }

    /**当反贼或忠臣被淘汰后，仅剩下内奸和另外一队，判定哪队胜利
     * @param identity 除内奸外存活的队伍*/
    private void twoTeam(Game game, Game.Identity identity) {
        var nei = Game.Identity.NEI; if (identity == nei) return; //防呆设计
        if (game.getScore(identity) > game.neiScore) game.win(identity);
        if (game.getScore(identity) < game.neiScore) game.win(nei);
    }

    public static final Map<ServerPlayerEntity, CardPileInventory> PLAYER_CARD_PACKS = new HashMap<>();

    @Override
    public void onStartTick(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();
        for (var player : playerList) {
            if (!PLAYER_CARD_PACKS.containsKey(player) && hasTrinket(ModItems.CARD_PILE, player)) PLAYER_CARD_PACKS.put(player, new CardPileInventory(player));
        }
        PLAYER_CARD_PACKS.keySet().removeIf(player -> player == null || player.isRemoved() || !hasTrinket(ModItems.CARD_PILE, player));
    }
}
