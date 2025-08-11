package com.amotassic.dabaosword.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleJsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceFinder finder;

    public SimpleJsonDataLoader(ResourceFinder finder) {
        this.finder = finder;
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, JsonElement> results = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : finder.findResources(manager).entrySet()) {
            Identifier identifier = entry.getKey();
            Identifier identifier2 = finder.toResourceId(identifier);

            try {
                Reader reader = entry.getValue().getReader();
                JsonElement jsonElement = StrictJsonParser.parse(reader);
                if (results.put(identifier2, jsonElement) == null) continue;
                throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, exception);
            }
        }
        return results;
    }
}
