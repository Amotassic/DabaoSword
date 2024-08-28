package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.LootEntry;
import com.amotassic.dabaosword.util.LootTableParser;
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

import static com.amotassic.dabaosword.util.ModTools.*;

public class GainCardItem extends CardItem {
    public GainCardItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
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
                CardUsePostCallback.EVENT.invoker().cardUsePost(user, user.getStackInHand(hand), null);
                voice(user, Sounds.WUZHONG);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void draw(PlayerEntity player, int count) {
        for (int n = 0; n<count; n++) {
            List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/draw.json"));
            LootEntry selectedEntry = GiftBoxItem.selectRandomEntry(lootEntries);

            give(player, new ItemStack(Registries.ITEM.get(selectedEntry.item())));
        }
    }
}
