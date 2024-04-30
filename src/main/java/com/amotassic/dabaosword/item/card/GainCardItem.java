package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.LootEntry;
import com.amotassic.dabaosword.util.LootTableParser;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class GainCardItem extends CardItem implements ModTools {
    public GainCardItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int m;
            //摸牌
            if (user.getStackInHand(hand).getItem() == ModItems.GAIN_CARD) {
                if (user.isSneaking()) {m=user.getStackInHand(hand).getCount();} else {m=1;}
                draw(user,m);voice(user, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
                if (!user.isCreative()) {user.getStackInHand(hand).decrement(m);}
            }
            //无中生有
            if (user.getStackInHand(hand).getItem() == ModItems.WUZHONG) {
                draw(user,2);
                if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
                voice(user, Sounds.WUZHONG);
                jizhi(user); benxi(user);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void draw(PlayerEntity player, int count) {
        for (int n = 0; n<count; n++) {
            List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/draw.json"));
            LootEntry selectedEntry = selectRandomEntry(lootEntries);

            player.giveItemStack(new ItemStack(Registries.ITEM.get(selectedEntry.item())));
        }
    }

    private static LootEntry selectRandomEntry(List<LootEntry> lootEntries) {
        double totalWeight = lootEntries.stream().mapToDouble(LootEntry::weight).sum();
        double randomValue = new Random().nextDouble() * totalWeight;
        double currentWeight = 0;
        for (LootEntry entry : lootEntries) {
            currentWeight += entry.weight();
            if (randomValue < currentWeight) {
                return entry;
            }
        }
        // 如果没有匹配的条目，默认返回最后一个条目
        return lootEntries.get(lootEntries.size() - 1);
    }
}
