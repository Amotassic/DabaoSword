package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider {
    public AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> r) {
        super(output, r);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
        ADVANCEMENTS.keySet().forEach(name -> consumer.accept(get(name)));
    }

    private static final Map<String, Advancement.Builder> ADVANCEMENTS = new HashMap<>();

    private static AdvancementEntry add(String name, Advancement.Builder builder) {
        ADVANCEMENTS.put(name, builder);
        Identifier id = Identifier.of("dabaosword", name);
        return builder.build(id);
    }

    public static AdvancementEntry get(String name) {
        Identifier id = Identifier.of("dabaosword", name);
        return ADVANCEMENTS.get(name).build(id);
    }

    private static final AdvancementEntry root = add("root", Advancement.Builder.create()
            .rewards(AdvancementRewards.Builder.function(Identifier.of("dabaosword", "root")).build())
            .display(ModItems.BBJI,
                    Text.translatable("advancement.dabaosword.root.title"),
                    Text.translatable("advancement.dabaosword.root.tip"),
                    Identifier.of("textures/gui/advancements/backgrounds/adventure.png"),
                    AdvancementFrame.TASK, true, false, false
            ).criterion("root", TickCriterion.Conditions.createTick()));

    static {
        add("card_pile", Advancement.Builder.create().parent(root)
                .display(ModItems.CARD_PILE,
                        Text.translatable("advancement.dabaosword.card_pile.title"),
                        Text.translatable("advancement.dabaosword.card_pile.tip"),
                        null,
                        AdvancementFrame.TASK, true, true, false
                ).criterion("card_pile", InventoryChangedCriterion.Conditions.items(ModItems.CARD_PILE)));

        add("gift_box", Advancement.Builder.create().parent(root)
                .display(ModItems.GIFTBOX,
                        Text.translatable("advancement.dabaosword.gift_box.title"),
                        Text.translatable("advancement.dabaosword.gift_box.tip"),
                        null,
                        AdvancementFrame.TASK, true, true, false
                ).criterion("gift_box", InventoryChangedCriterion.Conditions.items(ModItems.GIFTBOX)));
    }
}
