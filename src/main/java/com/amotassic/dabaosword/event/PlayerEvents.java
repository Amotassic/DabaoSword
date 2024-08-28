package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.PlayerConnectCallback;
import com.amotassic.dabaosword.event.callback.PlayerDeathCallback;
import com.amotassic.dabaosword.event.callback.PlayerRespawnCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.*;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) {
                    ItemStack stack = inv.getStack(i);
                    if (stack.isIn(Tags.Items.CARD) || stack.getItem() == ModItems.GAIN_CARD) {
                        //如果没有触发行殇，才触发事件（即在玩家死亡弃牌事件中，行殇有最高触发优先级）
                        if (XingshangTrigger(player, stack)) CardDiscardCallback.EVENT.invoker().cardDiscard(player, stack, stack.getCount(), false);
                    }
                }

                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack = entry.getRight();
                        if(stack.isIn(Tags.Items.CARD)) {
                            if (XingshangTrigger(player, stack)) CardDiscardCallback.EVENT.invoker().cardDiscard(player, stack, stack.getCount(), true);
                        }
                    }
                }
            }

            if (hasItem(player, ModItems.BBJI)) voice(player, Sounds.XUYOU);

            if (hasTrinket(SkillCards.BUQU, player)) {
                ItemStack stack = trinketItem(SkillCards.BUQU, player);
                int c = getTag(stack);
                if (c > 1) {
                    setTag(stack, (c+1)/2);
                }
            }
        }
    }

    private static boolean XingshangTrigger(PlayerEntity player, ItemStack stack) {
        for (PlayerEntity player1 : player.getWorld().getPlayers()) {//行殇技能触发
            if (hasTrinket(SkillCards.XINGSHANG, player1) && player1.distanceTo(player) <= 25 && player1 != player) {
                if (!player1.getCommandTags().contains("xingshang")) {
                    if (new Random().nextFloat() < 0.5) voice(player1, Sounds.XINGSHANG1); else voice(player1, Sounds.XINGSHANG2);
                }
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
            if (card) {
                give(player, new ItemStack(ModItems.SHA));
                give(player, new ItemStack(ModItems.SHAN));
                give(player, new ItemStack(ModItems.PEACH));
            }

        }
    }
}
