package com.amotassic.dabaosword.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LootTableParser {
    //自定义战利品表解析
    public static List<LootEntry> parseLootTable(Identifier lootTableId) {
        Gson gson = new Gson();
        List<LootEntry> lootEntries = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(LootTableParser.class.getResourceAsStream("/data/dabaosword/" + lootTableId.getPath())));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        if (jsonObject.has("results")) {
            for (JsonElement element : jsonObject.getAsJsonArray("results")) {
                JsonObject result = element.getAsJsonObject();
                double weight = result.get("weight").getAsDouble();
                Identifier item = new Identifier(result.get("item").getAsString());
                lootEntries.add(new LootEntry(item, weight));
            }
        }
        return lootEntries;
    }
}
