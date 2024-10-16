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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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
                CardPileInventory inventory = new CardPileInventory(player); //移除牌堆背包的牌
                for (var stack : inventory.cards) {cardDiscard(player, stack, stack.getCount(), false);}

                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) { //移除玩家物品栏的牌
                    ItemStack stack = inv.getStack(i);
                    if (isCard(stack)) cardDiscard(player, stack, stack.getCount(), false);
                }

                for(var pair : allTrinkets(player)) { //移除玩家装备区的牌
                    ItemStack stack = pair.getRight();
                    if(isCard(stack)) cardDiscard(player, stack, stack.getCount(), true);
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
