package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider {
    protected AdvancementsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.@NonNull Provider provider, @NonNull Consumer<AdvancementHolder> consumer) {
        ADVANCEMENTS.keySet().forEach(name -> consumer.accept(get(name)));
    }

    private static final Map<String, Advancement.Builder> ADVANCEMENTS = new HashMap<>();

    private static AdvancementHolder add(String name, Advancement.Builder builder) {
        ADVANCEMENTS.put(name, builder);
        Identifier id = DabaoSword.id(name);
        return builder.build(id);
    }

    public static AdvancementHolder get(String name) {
        Identifier id = DabaoSword.id(name);
        return ADVANCEMENTS.get(name).build(id);
    }

    private static final AdvancementHolder root = add("root", Advancement.Builder.advancement()
            .rewards(AdvancementRewards.Builder.function(DabaoSword.id("root")).build())
            .display(ModItems.BBJI,
                    Component.translatable("advancement.dabaosword.root.title"),
                    Component.translatable("advancement.dabaosword.root.tip"),
                    Identifier.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png"),
                    AdvancementType.TASK, true, false, false
            ).addCriterion("root", PlayerTrigger.TriggerInstance.tick()));

    static {
        add("card_pile", Advancement.Builder.advancement().parent(root)
                .display(ModItems.CARD_PILE,
                        Component.translatable("advancement.dabaosword.card_pile.title"),
                        Component.translatable("advancement.dabaosword.card_pile.tip"),
                        null,
                        AdvancementType.TASK, true, true, false
                ).addCriterion("card_pile", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CARD_PILE)));

        add("gift_box", Advancement.Builder.advancement().parent(root)
                .display(ModItems.GIFTBOX,
                        Component.translatable("advancement.dabaosword.gift_box.title"),
                        Component.translatable("advancement.dabaosword.gift_box.tip"),
                        null,
                        AdvancementType.TASK, true, true, false
                ).addCriterion("gift_box", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.GIFTBOX)));
    }
}
