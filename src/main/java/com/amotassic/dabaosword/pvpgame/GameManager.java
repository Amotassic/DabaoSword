package com.amotassic.dabaosword.pvpgame;

import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.util.ModConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager extends PersistentState {
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

    public static PersistentStateType<GameManager> getPersistentStateType() {
        return new PersistentStateType<>("dabaosword_game", GameManager::new, CODEC, null);
    }

    public GameManager() {this.markDirty();}
    public GameManager(List<GameManager.GameWithId> games, int nextAvailableId) {
        for (var gameWithId : games) this.games.put(gameWithId.id, gameWithId.game);
        this.nextAvailableId = nextAvailableId;
        markDirty();
    }

    public int getGameCount() {return games.size();}

    @Nullable
    public Game createGame(ServerPlayerEntity player, int type) {
        Box box = new Box(player.getBlockPos()).expand(ModConfig.SearchRadius);
        List<PlayerEntity> players = player.getWorld().getEntitiesByClass(PlayerEntity.class, box, p -> !p.isSpectator() && !isPlayerInGame(p));
        if (players.size() < 2) {
            player.sendMessage(Text.literal("Not enough players to start a game!").formatted(Formatting.RED));
            return null;
        }
        Set<UUID> playerUuids = new HashSet<>();
        for (PlayerEntity p : players) playerUuids.add(p.getUuid());
        Game game = new Game(nextId(), playerUuids, type);
        games.put(game.getGameId(), game);
        PVPGameEvents.onGameCreate(player, game, playerUuids);
        markDirty();
        return game;
    }

    @Nullable
    public Game getGameByPlayer(PlayerEntity player) {
        for (Game game : games.values()) {
            if (game.isPlayerInThisGame(player)) return game;
        }
        return null;
    }

    /**判断玩家是否已经加入任意一场对战*/
    public boolean isPlayerInGame(PlayerEntity player) {return getGameByPlayer(player) != null;}

    public void tick(ServerWorld world) {
        Iterator<Game> iterator = this.games.values().iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (!game.isActive()) { //移除游戏
                iterator.remove();
                markDirty();
                continue;
            }
            game.tick(world);
        }
        //if (world.getTime() % 200 == 0) System.out.println("GameManager tick: " + games.keySet());
        if (world.getTime() % 200 == 0) markDirty();
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
