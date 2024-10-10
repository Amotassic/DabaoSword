package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.PlayerConnectCallback;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.api.event.PlayerRespawnCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerEvents implements PlayerConnectCallback, PlayerDeathCallback, PlayerRespawnCallback {
    @Override
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {

        if (!player.getCommandTags().contains("given_skill")) {
            SkillItem.changeSkill(player);
            player.addCommandTag("given_skill");
        }

    }

    @Override
    public void onDeath(ServerPlayerEntity player, DamageSource source) {
        if (player.getWorld() instanceof ServerWorld world) {
            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card) {
                CardPileInventory cards = new CardPileInventory(player);
                for (var stack : cards.cards) {
                    if (XingshangTrigger(player, stack)) cardDiscard(player, new Pair<>(cards, stack), stack.getCount(), false);
                }

                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) {
                    ItemStack stack = inv.getStack(i);
                    if (isCard(stack)) {
                        //如果没有触发行殇，才触发事件（即在玩家死亡弃牌事件中，行殇有最高触发优先级）
                        if (XingshangTrigger(player, stack)) cardDiscard(player, stack, stack.getCount(), false);
                    }
                }

                for(var pair : allTrinkets(player)) {
                    ItemStack stack = pair.getRight();
                    if(isCard(stack)) {
                        if (XingshangTrigger(player, stack)) cardDiscard(player, stack, stack.getCount(), true);
                    }
                }
            }

            if (hasItem(player, stack -> stack.isOf(ModItems.BBJI))) voice(player, Sounds.XUYOU);

            if (hasTrinket(SkillCards.BUQU, player)) {
                ItemStack stack = trinketItem(SkillCards.BUQU, player);
                int c = getTag(stack);
                if (c > 1) setTag(stack, (c+1)/2);
            }

            if (hasTrinket(SkillCards.LIANYING, player)) {
                ItemStack stack = trinketItem(SkillCards.LIANYING, player);
                if (stack != null) setCD(stack, 0);
            }
        }
    }

    private static boolean XingshangTrigger(PlayerEntity player, ItemStack stack) {
        for (PlayerEntity player1 : player.getWorld().getPlayers()) {//行殇技能触发
            if (hasTrinket(SkillCards.XINGSHANG, player1) && player1.distanceTo(player) <= 25 && player1 != player) {
                if (!player1.getCommandTags().contains("xingshang")) voice(player1, Sounds.XINGSHANG);
                player1.addCommandTag("xingshang");
                give(player1, stack.copy());
                stack.setCount(0);
                return false;
            }
        }//为了简便，触发行殇后返回false
        return true;
    }

    @Override
    public void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {

            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card && hasTrinket(ModItems.CARD_PILE, player)) {
                give(player, new ItemStack(ModItems.SHA));
                give(player, new ItemStack(ModItems.SHAN));
                give(player, new ItemStack(ModItems.PEACH));
            }

        }
    }
}
