package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.api.event.PlayerRespawnCallback;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.pvpgame.Game;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

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
    public void onDeath(ServerPlayer player, DamageSource source) {
        if (player.level() instanceof ServerLevel world) {
            Entity attacker = source.getEntity();
            if (!(attacker instanceof Player)) attacker = player.getKillCredit();

            if (ModConfig.KillStreak && attacker instanceof ServerPlayer killer) { //紫砂也算连上了
                UUID id = killer.getUUID(); long time = world.getGameTime();

                var data = playerKillData.getOrDefault(id, new KillStreakData(0, 0));
                long timeDiff = time - data.lastKillTime();
                int newStreak = (0 <= timeDiff && timeDiff <= 1200) ? data.streak() + 1 : 1;
                data = new KillStreakData(newStreak, time);
                playerKillData.put(id, data);

                if (data.streak() >= 2) voice(killer, getKillSound(data.streak()));
            }

            if (attacker instanceof ServerPlayer killer && killer != player) {
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
                    player.setGameMode(GameType.SPECTATOR);
                    game.forEachPlayer(p -> p.sendSystemMessage(Component.translatable("dabaosword.game.view_id.tip", player.getDisplayName(), Component.translatable(identity.tag)).withStyle(Game.getIdentityColor(identity), ChatFormatting.BOLD)));
                }
            }

            if (world.getGameRules().get(Gamerule.CLEAR_CARDS_AFTER_DEATH)) {
                CardEvents.cardDiscard(player, cardsToDrop(player));
            }

            if (hasItem(player, p(ModItems.BBJI))) voice(player, "xuyou");
            player.entityTags().remove("duanchang");
        }
    }

    public static ExData cardsToDrop(Player player) {
        var data = d();
        var inventory = getCardPack(player);
        for (var stack : inventory.cards) data.cards(stack, stack.getCount());

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (isCard(stack)) data.cards(stack, stack.getCount());
        }

        for (var stack : allTrinkets(player)) {
            if(isCard(stack)) data.cards(stack, stack.getCount(), true);
        }
        return data;
    }

    @Override
    public void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer player) {
        if (player.level() instanceof ServerLevel world) {

            boolean card = world.getGameRules().get(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card && hasTrinket(ModItems.CARD_PILE, player)) {
                give(player, newCard(ModItems.SHA));
                give(player, newCard(ModItems.SHAN));
                give(player, newCard(ModItems.PEACH));
                draw(player);
            }

        }
    }
}
