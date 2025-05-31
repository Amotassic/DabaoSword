package com.amotassic.dabaosword.pvpgame;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.event.PVPGameTickCallback;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.util.ModConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Game {
    public static final MapCodec<Game> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("id").forGetter(game -> game.id),
                            Uuids.SET_CODEC.fieldOf("players").forGetter(game -> game.players),
                            Codec.INT.fieldOf("type").forGetter(game -> game.type),
                            Codec.BOOL.fieldOf("active").forGetter(game -> game.active),
                            Codec.INT.fieldOf("count_down").forGetter(game -> game.countDown),
                            Codec.INT.fieldOf("game_time").forGetter(game -> game.gameTime),
                            Codec.INT.fieldOf("time_out").forGetter(game -> game.timeOut),
                            Codec.INT.fieldOf("zhong_lives").forGetter(game -> game.zhongLives),
                            Codec.INT.fieldOf("fan_lives").forGetter(game -> game.fanLives),
                            Codec.INT.fieldOf("nei_lives").forGetter(game -> game.neiLives),
                            Codec.INT.fieldOf("zhong_score").forGetter(game -> game.zhongScore),
                            Codec.INT.fieldOf("fan_score").forGetter(game -> game.fanScore),
                            Codec.INT.fieldOf("nei_score").forGetter(game -> game.neiScore)
                    )
                    .apply(instance, Game::new)
    );
    private final int id;
    private final Set<UUID> players = new HashSet<>();
    private final int type;
    private boolean active;
    private int countDown;
    private int gameTime;
    private int timeOut;
    //列举各项数据
    public int zhongLives, fanLives, neiLives;
    public int zhongScore, fanScore, neiScore;
    private Map<String, Integer> primaryDataCache;
    public static final String FANCOUNT = "fanCount", NEICOUNT = "neiCount", ZHONGCOUNT = "zhongCount", ZHONGLIVES = "zhongLives", FANLIVES = "fanLives", NEILIVES = "neiLives";

    public Game(int id, Set<UUID> players, int type) {
        this.id = id;
        this.players.addAll(players);
        this.type = type;
        this.active = true;
        int waitTime = ModConfig.WaitTime > 5 ? ModConfig.WaitTime : 5;
        this.countDown = waitTime * 20;
        this.gameTime = 0;
        this.timeOut = ModConfig.TimeOut;
        initData();
    }

    private Game(int id, Set<UUID> players, int type, boolean active, int countDown, int gameTime, int timeOut, int zhongLives, int fanLives, int neiLives, int zhongScore, int fanScore, int neiScore) {
        this.id = id;
        this.players.addAll(players);
        this.type = type;
        this.active = active;
        this.countDown = countDown;
        this.gameTime = gameTime;
        this.timeOut = timeOut;
        this.zhongLives = zhongLives;
        this.fanLives = fanLives;
        this.neiLives = neiLives;
        this.zhongScore = zhongScore;
        this.fanScore = fanScore;
        this.neiScore = neiScore;
    }

    public Map<String, Integer> getPrimaryData() {
        if (primaryDataCache != null) return primaryDataCache;

        Map<String, Integer> data = new HashMap<>();
        int playerCount = getPlayers().size();
        int fanCount = playerCount / 2;
        int neiCount = playerCount > 2 ? 1 : 0; //如果是无内奸模式，参与人数为偶数时，不设置内奸
        if (this.type == 1 && playerCount % 2 == 0) neiCount = 0;
        int zhongCount = playerCount - fanCount - neiCount;
        int zhongLives, fanLives, neiLives;
        zhongLives = fanLives = fanCount * 3;
        neiLives = neiCount > 0 ? fanCount * 3 : 0;
        data.put(FANCOUNT, fanCount); data.put(NEICOUNT, neiCount); data.put(ZHONGCOUNT, zhongCount);
        data.put(ZHONGLIVES, zhongLives); data.put(FANLIVES, fanLives); data.put(NEILIVES, neiLives);
        primaryDataCache = data;
        return data;
    }

    private void initData() {
        //根据人数随机分配身份
        var primaryData = getPrimaryData();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < primaryData.get(ZHONGCOUNT); i++) ids.add(Identity.ZHONG.tag);
        for (int i = 0; i < primaryData.get(FANCOUNT); i++) ids.add(Identity.FAN.tag);
        for (int i = 0; i < primaryData.get(NEICOUNT); i++) ids.add(Identity.NEI.tag);
        Collections.shuffle(ids); // 随机打乱列表中的元素
        forEachPlayer(player -> { //确保移除所有的身份标签再添加新的身份标签
            player.getCommandTags().remove("dabaosword.zhong");
            player.getCommandTags().remove("dabaosword.fan");
            player.getCommandTags().remove("dabaosword.nei");
            player.addCommandTag(ids.removeFirst());
        });
        this.zhongLives = primaryData.get(ZHONGLIVES);
        this.fanLives = primaryData.get(FANLIVES);
        this.neiLives = primaryData.get(NEILIVES);
        this.zhongScore = this.fanScore = this.neiScore = 0;
    }

    public boolean isPlayerInThisGame(PlayerEntity player) {
        return this.players.contains(player.getUuid());
    }

    public int getGameId() {return id;}

    /**游戏被加载，不论是等待中还是已经开始*/
    public boolean isActive() {return active;}

    public Set<UUID> getPlayers() {return players;}

    /**游戏处于准备阶段倒计时，此时玩家可以拒绝加入游戏*/
    public boolean isWaiting() {return countDown > 0;}

    public int getCountDown() {return countDown;}

    public int getGameTime() {return gameTime;}

    public int getTimeOut() {return timeOut;}

    /**游戏已经开始，且不处于准备阶段*/
    public boolean isOn() {return getGameTime() > 0;}

    public void refuseGame(PlayerEntity player) {
        if (!isWaiting()) return;
        discardGame();
        forEachPlayer(p -> {
            p.sendMessage(Text.translatable("dabaosword.game.refuse", player.getDisplayName()).formatted(Formatting.RED));
            voice(p, SoundEvents.ITEM_SHIELD_BREAK.value());
        });
    }

    public void win(Identity identity) {
        forEachPlayer(player -> {
            if (getIdentity(player) == identity) {
                voice(player, "win");
                title(player, Text.translatable("dabaosword.game.win").formatted(Formatting.GOLD));
            }
            player.sendMessage(Text.translatable("dabaosword.game.end", Text.translatable(identity.tag)).formatted(getIdentityColor(identity)));
        });
        discardGame();
    }

    public void timeOut() {
        forEachPlayer(player -> player.sendMessage(Text.translatable("dabaosword.game.timeout").formatted(Formatting.RED)));
        Integer max = findUniqueMax(zhongScore, fanScore, neiScore);
        if (max == null) discardGame();
        else if (zhongScore == max) win(Identity.ZHONG);
        else if (fanScore == max) win(Identity.FAN);
        else if (neiScore == max) win(Identity.NEI);
    }

    public void discardGame() {
        this.active = false;
        var scoreboard = DabaoSword.server.getScoreboard();
        var obj = scoreboard.getObjectives().stream().filter(o -> o.getName().equals("dabaosword.death")).findFirst().orElse(null);
        if (obj != null && PVPGameEvents.getGameManager().getGameCount() <= 1) scoreboard.removeObjective(obj);
        forEachPlayer(player -> {
            player.getCommandTags().remove("dabaosword.zhong");
            player.getCommandTags().remove("dabaosword.fan");
            player.getCommandTags().remove("dabaosword.nei");
            if (player.isSpectator()) {
                player.changeGameMode(GameMode.SURVIVAL); player.kill(world(player));
            }
        });
    }

    public void tick(ServerWorld world) {
        if (!this.active) return;
        PVPGameTickCallback.EVENT.invoker().onGameTick(this, world);
        //倒计时为-1时，游戏开始计时
        if (this.countDown > -1) --this.countDown; else ++this.gameTime;
        if (isOn() && getGameTime() % 20 == 0) --this.timeOut;
    }

    public void forEachPlayer(Consumer<ServerPlayerEntity> action) {
        for (UUID uuid : getPlayers()) {
            ServerPlayerEntity player = DabaoSword.server.getPlayerManager().getPlayer(uuid);
            if (player == null) continue;
            action.accept(player);
        }
    }

    public int getRespawnChances(Identity identity) {
        var data = getPrimaryData();
        return switch (identity) {
            case ZHONG -> zhongLives - data.get(ZHONGCOUNT) + 1;
            case FAN -> fanLives - data.get(FANCOUNT) + 1;
            case NEI -> neiLives - data.get(NEICOUNT) + 1;
        };
    }

    public int getLives(Identity identity) {
        return switch (identity) {
            case ZHONG -> zhongLives;
            case FAN -> fanLives;
            case NEI -> neiLives;
        };
    }

    public void setLives(Identity identity, int lives) {
        switch (identity) {
            case ZHONG -> zhongLives = lives;
            case FAN -> fanLives = lives;
            case NEI -> neiLives = lives;
        }
    }

    /**减少该玩家所在队伍的剩余生命数（等于0不会减少），玩家死亡时调用*/
    public void decreaseLives(ServerPlayerEntity player) {
        Identity identity = getIdentity(player);
        int lives = getLives(identity);
        if (lives > 0) setLives(identity, lives - 1);
    }

    public int getScore(Identity identity) {
        return switch (identity) {
            case ZHONG -> zhongScore;
            case FAN -> fanScore;
            case NEI -> neiScore;
        };
    }

    public void setScore(Identity identity, int score) {
        switch (identity) {
            case ZHONG -> zhongScore = score;
            case FAN -> fanScore = score;
            case NEI -> neiScore = score;
        }
    }

    /**增加该玩家所在队伍的分数，同时向所有玩家播报分数*/
    public void increaseScore(ServerPlayerEntity player) {
        Identity identity = getIdentity(player);
        setScore(identity, getScore(identity) + 1);
        this.timeOut = ModConfig.TimeOut;
        forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.score.add", player.getDisplayName()).formatted(Formatting.BOLD)));
    }

    /**确保玩家在该对局中才可以调用本方法*/
    public Identity getIdentity(ServerPlayerEntity player) {
        if (player.getCommandTags().contains(Identity.ZHONG.tag)) return Identity.ZHONG;
        if (player.getCommandTags().contains(Identity.FAN.tag)) return Identity.FAN;
        return Identity.NEI;
    }

    public static Integer findUniqueMax(int... numbers) {
        if (numbers.length == 0) return null;
        int max = numbers[0];
        boolean isUnique = true;
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
                isUnique = true;
            } else if (numbers[i] == max) {isUnique = false;}
        }
        return isUnique ? max : null;
    }

    public static Formatting getIdentityColor(Identity identity) {
        return switch (identity) {
            case ZHONG -> Formatting.YELLOW;
            case FAN -> Formatting.GREEN;
            case NEI -> Formatting.BLUE;
        };
    }

    public enum Identity {
        ZHONG("dabaosword.zhong"),
        FAN("dabaosword.fan"),
        NEI("dabaosword.nei");

        public final String tag;

        Identity(String tag) {
            this.tag = tag;
        }
    }
}
