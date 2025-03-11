package com.amotassic.dabaosword.pvpgame;

import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager extends PersistentState {
    private final Map<Integer, Game> games = new HashMap<>();
    private final ServerWorld world;
    private int nextAvailableId;

    public static PersistentState.Type<GameManager> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<>(() -> new GameManager(world), (nbt, r) -> GameManager.fromNbt(world, nbt), null);
    }

    public GameManager(ServerWorld world) {
        this.world = world;
        this.nextAvailableId = 1;
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
        Game game = new Game(nextId(), world, playerUuids, type);
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

    public void tick() {
        Iterator<Game> iterator = this.games.values().iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (!game.isActive()) { //移除游戏
                iterator.remove();
                markDirty();
                continue;
            }
            game.tick();
        }
        //if (world.getTime() % 200 == 0) System.out.println("GameManager tick: " + games.keySet());
        if (world.getTime() % 200 == 0) markDirty();
    }

    public static GameManager fromNbt(ServerWorld world, NbtCompound nbt) {
        GameManager gameManager = new GameManager(world);
        gameManager.nextAvailableId = nbt.getInt("NextAvailableID");
        NbtList nbtList = nbt.getList("Games", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Game game = new Game(world, nbtCompound);
            gameManager.games.put(game.getGameId(), game);
        }
        return gameManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("NextAvailableID", this.nextAvailableId);
        NbtList nbtList = new NbtList();
        for (Game game : games.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            game.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        nbt.put("Games", nbtList);
        return nbt;
    }

    private int nextId() {return ++nextAvailableId;}
}
