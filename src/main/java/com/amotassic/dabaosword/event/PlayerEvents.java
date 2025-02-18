package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.api.event.PlayerRespawnCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.pvpgame.Game;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import static com.amotassic.dabaosword.api.event.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.event.PVPGameEvents.getGameManager;
import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerEvents implements PlayerDeathCallback, PlayerRespawnCallback {
    @Override
    public void onDeath(ServerPlayerEntity player, DamageSource source) {
        if (player.getWorld() instanceof ServerWorld world) {

            if (source.getAttacker() instanceof ServerPlayerEntity killer && killer != player) {
                Game game = getGameManager().getGameByPlayer(killer);
                if (game != null && game.isOn() && game.isPlayerInThisGame(player)) {
                    var killerTeam = game.getIdentity(killer); var deadTeam = game.getIdentity(player);
                    if (deadTeam != Game.Identity.NEI && killerTeam != deadTeam) game.increaseScore(killer);
                }
            }

            //玩家死亡时，若处于对战中，减少该玩家所在队伍的剩余生命数
            Game game = getGameManager().getGameByPlayer(player);
            if (game != null && game.isOn()) {
                game.decreaseLives(player);
                var identity = game.getIdentity(player);
                int restLives = game.getLives(identity); //如果玩家所在阵营剩余生命值为0，公布玩家身份
                if (restLives == 0) game.forEachPlayer(p -> p.sendMessage(Text.translatable("dabaosword.game.view_id.tip", player.getDisplayName(), Text.translatable(identity.tag)).formatted(Game.getIdentityColor(identity))));
            }

            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card) {
                CardPileInventory inventory = new CardPileInventory(player); //移除牌堆背包的牌
                for (var stack : inventory.cards) {cardDiscard(player, stack, stack.getCount(), false);}

                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) { //移除玩家物品栏的牌
                    ItemStack stack = inv.getStack(i);
                    if (isCard(stack)) cardDiscard(player, stack, stack.getCount(), false);
                }

                for (var stack : allTrinkets(player)) { //移除玩家装备区的牌
                    if(isCard(stack)) cardDiscard(player, stack, stack.getCount(), true);
                }
            }

            if (hasItem(player, p(ModItems.BBJI))) voice(player, Sounds.XUYOU);

            if (hasTrinket(SkillCards.TAOLUAN, player)) {
                ItemStack stack = trinketItem(SkillCards.TAOLUAN, player);
                NbtCompound nbt = stack.getOrCreateNbt();
                nbt.remove("used"); stack.setNbt(nbt);
            }

            if (hasTrinket(SkillCards.BUQU, player)) {
                ItemStack stack = trinketItem(SkillCards.BUQU, player);
                int c = getTag(stack);
                if (c > 1) setTag(stack, (c+1)/2);
            }

            if (hasTrinket(SkillCards.LIANYING, player)) setCD(trinketItem(SkillCards.LIANYING, player), 0);
        }
    }

    @Override
    public void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            //玩家复活时，若其队伍剩余生命值为0，切换至旁观者模式
            Game game = getGameManager().getGameByPlayer(player);
            if (game != null) {
                int restLives = game.getLives(game.getIdentity(player));
                if (restLives == 0) player.changeGameMode(GameMode.SPECTATOR);
            }

            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card && hasTrinket(ModItems.CARD_PILE, player)) {
                give(player, new ItemStack(ModItems.SHA));
                give(player, new ItemStack(ModItems.SHAN));
                give(player, new ItemStack(ModItems.PEACH));
                draw(player);
            }

        }
    }
}
