package com.amotassic.dabaosword.pvpgame;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.util.ModConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class GameManager extends SavedData {
    public static final Codec<GameManager> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            GameWithId.CODEC
                                    .listOf()
                                    .optionalFieldOf("games", List.of())
                                    .forGetter(gameManager -> gameManager.games.int2ObjectEntrySet().stream().map(GameManager.GameWithId::fromMapEntry).toList()),
                            Codec.INT.fieldOf("next_id").forGetter(raidManager -> raidManager.nextAvailableId)
                    )
                    .apply(instance, GameManager::new)
    );
    private final Int2ObjectMap<Game> games = new Int2ObjectOpenHashMap<>();
    private int nextAvailableId;

    public static SavedDataType<GameManager> getPersistentStateType() {
        return new SavedDataType<>(DabaoSword.id("dabaosword_game"), GameManager::new, CODEC, null);
    }

    public GameManager() {this.setDirty();}
    public GameManager(List<GameManager.GameWithId> games, int nextAvailableId) {
        for (var gameWithId : games) this.games.put(gameWithId.id, gameWithId.game);
        this.nextAvailableId = nextAvailableId;
        setDirty();
    }

    public int getGameCount() {return games.size();}

    @Nullable
    public Game createGame(ServerPlayer player, int type) {
        AABB box = new AABB(player.getOnPos()).inflate(ModConfig.SearchRadius);
        List<Player> players = player.level().getEntitiesOfClass(Player.class, box, p -> !p.isSpectator() && !isPlayerInGame(p));
        if (players.size() < 2) {
            player.sendSystemMessage(Component.literal("Not enough players to start a game!").withStyle(ChatFormatting.RED));
            return null;
        }
        Set<UUID> playerUuids = new HashSet<>();
        for (var p : players) playerUuids.add(p.getUUID());
        Game game = new Game(nextId(), playerUuids, type);
        games.put(game.getGameId(), game);
        PVPGameEvents.onGameCreate(player, game, playerUuids);
        setDirty();
        return game;
    }

    @Nullable
    public Game getGameByPlayer(Player player) {
        for (Game game : games.values()) {
            if (game.isPlayerInThisGame(player)) return game;
        }
        return null;
    }

    /**判断玩家是否已经加入任意一场对战*/
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPlayerInGame(Player player) {return getGameByPlayer(player) != null;}

    public void tick(ServerLevel world) {
        Iterator<Game> iterator = this.games.values().iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (!game.isActive()) { //移除游戏
                iterator.remove();
                setDirty();
                continue;
            }
            game.tick(world);
        }
        //if (world.getTime() % 200 == 0) System.out.println("GameManager tick: " + games.keySet());
        if (world.getGameTime() % 200 == 0) setDirty();
    }

    private int nextId() {return ++nextAvailableId;}

    public record GameWithId(int id, Game game) {
        public static final Codec<GameManager.GameWithId> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.INT.fieldOf("id").forGetter(GameManager.GameWithId::id), Game.CODEC.forGetter(GameManager.GameWithId::game))
                        .apply(instance, GameManager.GameWithId::new)
        );

        public static GameManager.GameWithId fromMapEntry(Int2ObjectMap.Entry<Game> entry) {
            return new GameManager.GameWithId(entry.getIntKey(), entry.getValue());
        }
    }
}
