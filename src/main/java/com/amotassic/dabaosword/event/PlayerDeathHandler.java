package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.*;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class PlayerDeathHandler implements PlayerDeathCallback {
    @Override
    public void onDeath(ServerPlayerEntity player, DamageSource source) {

        if (player.getWorld() instanceof ServerWorld world) {
            boolean card = world.getGameRules().getBoolean(Gamerule.CLEAR_CARDS_AFTER_DEATH);
            if (card) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.size(); ++i) {
                    ItemStack stack = inv.getStack(i);
                    if (stack.isIn(Tags.Items.CARD) || stack.getItem() == ModItems.GAIN_CARD) {
                        for (PlayerEntity player1 : world.getPlayers()) {//行殇技能触发
                            if (hasTrinket(SkillCards.XINGSHANG, player1) && player1.distanceTo(player) <= 25 && player1 != player) {
                                if (!player1.getCommandTags().contains("xingshang")) {
                                    if (new Random().nextFloat() < 0.5) voice(player1, Sounds.XINGSHANG1); else voice(player1, Sounds.XINGSHANG2);
                                }
                                player1.addCommandTag("xingshang");
                                give(player1, stack); break;
                            }
                        }
                        inv.removeStack(i);
                    }
                }

                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack = entry.getRight();
                        if(stack.isIn(Tags.Items.CARD)) {
                            for (PlayerEntity player1 : world.getPlayers()) {//行殇技能触发
                                if (hasTrinket(SkillCards.XINGSHANG, player1) && player1.distanceTo(player) <= 25 && player1 != player) {
                                    if (!player1.getCommandTags().contains("xingshang")) {
                                        if (new Random().nextFloat() < 0.5) voice(player1, Sounds.XINGSHANG1); else voice(player1, Sounds.XINGSHANG2);
                                    }
                                    player1.addCommandTag("xingshang");
                                    give(player1, stack); break;
                                }
                            }
                            stack.setCount(0);
                        }
                    }
                }
            }

            if (hasItem(player, ModItems.BBJI)) voice(player, Sounds.XUYOU);

            if (hasTrinket(SkillCards.BUQU, player)) {
                ItemStack stack = trinketItem(SkillCards.BUQU, player);
                int c = stack.getNbt() == null ? 0 : stack.getNbt().getInt("buqu");
                if (c > 1) {
                    NbtCompound nbt = new NbtCompound(); nbt.putInt("buqu", (c+1)/2); stack.setNbt(nbt);
                }
            }
        }
    }
}
