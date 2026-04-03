package com.amotassic.dabaosword.data;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.item.card.CardItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardSuitAndRank extends SimpleJsonDataLoader {
    private static final FileToIdConverter FINDER = FileToIdConverter.json("default_suit_and_rank");
    public static final Identifier ID = DabaoSword.id("default_suit_and_rank");
    private static final Map<CardItem, JsonArray> cards = new HashMap<>();
    // 所有的牌
    private static final List<ItemStack> ALL_CARDS = new ArrayList<>();

    public CardSuitAndRank() {super(FINDER);}

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        ALL_CARDS.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            // System.out.println(entry.getKey() + " " + entry.getValue());
            Identifier key = DabaoSword.id("card/" + entry.getKey().getPath());
            JsonElement jsonElement = entry.getValue();
            try {
                Item item = BuiltInRegistries.ITEM.getValue(key);
                if (item instanceof CardItem cardItem) {
                    try {
                        cards.put(cardItem, jsonElement.getAsJsonObject().get("suits_and_ranks").getAsJsonArray());
                    } catch (Exception e) {
                        DabaoSword.LOGGER.error("Missing Element: suits_and_ranks", e);
                    }
                }
            } catch (Exception e) {
                DabaoSword.LOGGER.error("No such card registered: {}", key);
            }
        }
    }

    public static List<ItemStack> getAllCards() {
        if (ALL_CARDS.isEmpty()) {
            cards.forEach(((cardItem, srs) -> {
                for (int j = 0; j < srs.size(); j++) {
                    JsonObject sr = srs.get(j).getAsJsonObject();
                    String suit = sr.get("suit").getAsString();
                    String rank = sr.get("rank").getAsString();

                    Card card = new Card(cardItem, Suit.valueOf(suit), Rank.fromString(rank));
                    ALL_CARDS.add(card.toStack());
                }
            }));
            DabaoSword.LOGGER.info("Loaded {} cards", ALL_CARDS.size());
        }
        return ALL_CARDS;
    }
}
