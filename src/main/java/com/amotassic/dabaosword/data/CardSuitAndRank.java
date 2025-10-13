package com.amotassic.dabaosword.data;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.item.card.CardItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardSuitAndRank extends SimpleJsonDataLoader {
    private static final ResourceFinder FINDER = ResourceFinder.json("default_suit_and_rank");
    public static final Identifier ID = Identifier.of("dabaosword", "default_suit_and_rank");
    // 所有的牌
    public static final List<ItemStack> ALL_CARDS = new ArrayList<>();

    public CardSuitAndRank() {super(FINDER);}

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ALL_CARDS.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            // System.out.println(entry.getKey() + " " + entry.getValue());
            Identifier key = entry.getKey();
            JsonElement jsonElement = entry.getValue();
            try {
                Item item = Registries.ITEM.get(key);
                if (item instanceof CardItem cardItem) {
                    try {
                        JsonArray srs = jsonElement.getAsJsonObject().get("suits_and_ranks").getAsJsonArray();
                        for (int j = 0; j < srs.size(); j++) {
                            JsonObject sr = srs.get(j).getAsJsonObject();
                            String suit = sr.get("suit").getAsString();
                            String rank = sr.get("rank").getAsString();

                            Card card = new Card(cardItem, Suit.valueOf(suit), Rank.fromString(rank));
                            ALL_CARDS.add(card.toStack());
                        }
                    } catch (Exception e) {
                        DabaoSword.LOGGER.error("Missing Element: suits_and_ranks");
                    }
                }
            } catch (Exception e) {
                DabaoSword.LOGGER.error("No such card registered: {}", key);
            }
        }
        DabaoSword.LOGGER.info("Loaded {} cards", ALL_CARDS.size());
    }
}
