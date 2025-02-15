package com.amotassic.dabaosword.pvpgame;

import com.amotassic.dabaosword.api.event.PVPGameTickCallback;
import com.amotassic.dabaosword.event.PVPGameEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Consumer;

public class Game {
    private final ServerWorld world;
    private final int id;
    private final Set<UUID> players = new HashSet<>();
    private boolean active;
    private int countDown;
    private int gameTime;
    //列举各项数据
    public int zhongLives;
    public int fanLives;
    public int neiLives;
    public int zhongScore;
    public int fanScore;
    public int neiScore;

    public Game(int id, ServerWorld world, Set<UUID> players) {
        this.world = world;
        this.id = id;
        this.players.addAll(players);
        this.active = true;
        this.countDown = 10 * 20;
        this.gameTime = 0;
        initData();
    }

    public Game(ServerWorld world, NbtCompound nbt) {
        this.world = world;
        this.id = nbt.getInt("Id");
        NbtList nbtList = nbt.getList("Players", NbtElement.INT_ARRAY_TYPE);
        for (NbtElement nbtElement : nbtList) {
            this.players.add(NbtHelper.toUuid(nbtElement));
        }
        this.active = nbt.getBoolean("Active");
        this.countDown = nbt.getInt("CountDown");
        this.gameTime = nbt.getInt("GameTime");
        this.zhongLives = nbt.getInt("ZhongLives");
        this.fanLives = nbt.getInt("FanLives");
        this.neiLives = nbt.getInt("NeiLives");
        this.zhongScore = nbt.getInt("ZhongScore");
        this.fanScore = nbt.getInt("FanScore");
        this.neiScore = nbt.getInt("NeiScore");
    }

    private void initData() {
        //根据人数随机分配身份
        int playerCount = this.players.size();
        int fanCount = playerCount / 2;
        int neiCount = playerCount > 2 ? 1 : 0;
        int zhongCount = playerCount - fanCount - neiCount;
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < zhongCount; i++) ids.add(Identity.ZHONG.tag);
        for (int i = 0; i < fanCount; i++) ids.add(Identity.FAN.tag);
        for (int i = 0; i < neiCount; i++) ids.add(Identity.NEI.tag);
        Collections.shuffle(ids); // 随机打乱列表中的元素
        forEachPlayer(player -> { //确保移除所有的身份标签再添加新的身份标签
            player.getCommandTags().remove("dabaosword.zhong");
            player.getCommandTags().remove("dabaosword.fan");
            player.getCommandTags().remove("dabaosword.nei");
            player.addCommandTag(ids.remove(0));
        });
        this.zhongLives = this.fanLives = this.neiLives = fanCount * 3;
        this.zhongScore = this.fanScore = this.neiScore = 0;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", this.id);
        NbtList nbtList = new NbtList();
        for (UUID uuid : this.players) nbtList.add(NbtHelper.fromUuid(uuid));
        nbt.put("Players", nbtList);
        nbt.putBoolean("Active", this.active);
        nbt.putInt("CountDown", this.countDown);
        nbt.putInt("GameTime", this.gameTime);
        nbt.putInt("ZhongLives", this.zhongLives);
        nbt.putInt("FanLives", this.fanLives);
        nbt.putInt("NeiLives", this.neiLives);
        nbt.putInt("ZhongScore", this.zhongScore);
        nbt.putInt("FanScore", this.fanScore);
        nbt.putInt("NeiScore", this.neiScore);
    }

    public boolean isPlayerInThisGame(PlayerEntity player) {
        return this.players.contains(player.getUuid());
    }

    public int getGameId() {return id;}

    /**游戏被加载，不论是等待中还是已经开始*/
    public boolean isActive() {return active;}

    /**游戏处于准备阶段倒计时，此时玩家可以拒绝加入游戏*/
    public boolean isWaiting() {return countDown > 0;}

    public int getCountDown() {return countDown;}

    public int getGameTime() {return gameTime;}

    /**游戏已经开始，且不处于准备阶段*/
    public boolean isOn() {return getGameTime() > 0;}

    public void refuseGame(PlayerEntity player) {
        if (!isWaiting()) return;
        discardGame();
        forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.game.refuse", player.getDisplayName()).formatted(Formatting.RED)));
    }

    public void win(Identity identity) {
        forEachPlayer(player -> {
            player.sendMessage(Text.translatable("dabaosword.game.win", Text.translatable(identity.tag)).formatted(getIdentityColor(identity)));
        });
        discardGame();
    }

    public void discardGame() {
        this.active = false;
        var scoreboard = world.getServer().getScoreboard();
        var obj = scoreboard.getObjective("dabaosword.death");
        if (obj != null && PVPGameEvents.getGameManager().getGameCount() <= 1) scoreboard.removeObjective(obj);
        forEachPlayer(player -> {
            player.getCommandTags().remove("dabaosword.zhong");
            player.getCommandTags().remove("dabaosword.fan");
            player.getCommandTags().remove("dabaosword.nei");
            if (player.isSpectator()) {
                player.changeGameMode(GameMode.SURVIVAL); player.kill();
            }
        });
    }

    public void tick() {
        if (!this.active) return;
        PVPGameTickCallback.EVENT.invoker().onGameTick(this, world);
        //倒计时为-1时，游戏开始计时
        if (this.countDown > -1) --this.countDown; else ++this.gameTime;
    }

    public void forEachPlayer(Consumer<ServerPlayerEntity> action) {
        for (UUID uuid : this.players) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
            if (player == null) continue;
            action.accept(player);
        }
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
        forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.score.add", player.getDisplayName())));
    }

    /**确保玩家在该对局中才可以调用本方法*/
    public Identity getIdentity(ServerPlayerEntity player) {
        if (player.getCommandTags().contains(Identity.ZHONG.tag)) return Identity.ZHONG;
        if (player.getCommandTags().contains(Identity.FAN.tag)) return Identity.FAN;
        return Identity.NEI;
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
