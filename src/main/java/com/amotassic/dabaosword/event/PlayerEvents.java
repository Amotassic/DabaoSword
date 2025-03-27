package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.api.event.PlayerRespawnCallback;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.pvpgame.Game;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.amotassic.dabaosword.event.PVPGameEvents.getGameManager;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerEvents implements PlayerDeathCallback, PlayerRespawnCallback {
    private static final Map<UUID, KillStreakData> playerKillData = new HashMap<>();
    private record KillStreakData(int streak, long lastKillTime) {}
    private static String getKillSound(int streak) {
        return switch (streak) {
            case 1, 2, 3, 4, 5, 6, 7 -> "kill" + streak;
            default -> "diankuang";
        };
    }

    @Override
    public void onDeath(ServerPlayerEntity player, DamageSource source) {
        if (player.getWorld() instanceof ServerWorld world) {
            Entity attacker = source.getAttacker();
            if (!(attacker instanceof PlayerEntity)) attacker = player.getPrimeAdversary();

            if (ModConfig.KillStreak && attacker instanceof ServerPlayerEntity killer) { //紫砂也算连上了
                UUID id = killer.getUuid(); long time = world.getTime();

                var data = playerKillData.getOrDefault(id, new KillStreakData(0, 0));
                long timeDiff = time - data.lastKillTime();
                int newStreak = (0 <= timeDiff && timeDiff <= 1200) ? data.streak() + 1 : 1;
                data = new KillStreakData(newStreak, time);
                playerKillData.put(id, data);

                if (data.streak() >= 2) voice(killer, getKillSound(data.streak()));
            }

            if (attacker instanceof ServerPlayerEntity killer && killer != player) {
                Game game = getGameManager().getGameByPlayer(killer);
                if (game != null && game.isOn() && game.isPlayerInThisGame(player)) {
                    var primaryData = game.getPrimaryData();
                    if (ModConfig.KillStreak && game.zhongLives + game.fanLives + game.neiLives == primaryData.get(Game.ZHONGLIVES) + primaryData.get(Game.FANLIVES) + primaryData.get(Game.NEILIVES)) voice(killer, getKillSound(1));

                    var killerTeam = game.getIdentity(killer); var deadTeam = game.getIdentity(player);
                    if (deadTeam != Game.Identity.NEI && killerTeam != deadTeam) game.increaseScore(killer);
                }
            }

            //玩家死亡时，若处于对战中，减少该玩家所在队伍的剩余生命数
            Game game = getGameManager().getGameByPlayer(player);
            if (game != null && game.isOn()) {
                game.decreaseLives(player);
                var identity = game.getIdentity(player);
                int re = game.getRespawnChances(identity);
                if (re <= 0) { //如果玩家所在阵营剩余复活次数为0，公布玩家身份
                    player.changeGameMode(GameMode.SPECTATOR);
                    game.forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.game.view_id.tip", player.getDisplayName(), Text.translatable(identity.tag)).formatted(Game.getIdentityColor(identity), Formatting.BOLD)));
                }
            }

            if (world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH)) {
                var data = cardsToDrop(player);
                CardEvents.cardDiscard(player, data);
            }

            if (hasItem(player, p(ModItems.BBJI))) voice(player, "xuyou");
        }
    }

    public static ExData cardsToDrop(PlayerEntity player) {
        var data = d();
        var inventory = getCardPack(player);
        for (var stack : inventory.cards) data.cards(stack, stack.getCount());

        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getStack(i);
            if (isCard(stack)) data.cards(stack, stack.getCount());
        }

        for (var stack : allTrinkets(player)) {
            if(isCard(stack)) data.cards(stack, stack.getCount(), true);
        }
        return data;
    }

    @Override
    public void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {

            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card && hasTrinket(ModItems.CARD_PILE, player)) {
                give(player, newCard(ModItems.SHA));
                give(player, newCard(ModItems.SHAN));
                give(player, newCard(ModItems.PEACH));
                draw(player);
            }

        }
    }
}
