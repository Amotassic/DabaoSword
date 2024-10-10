package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.create()
                .display(ModItems.BBJI,
                        Text.translatable("advancement.dabaosword.root.title"),
                        Text.translatable("advancement.dabaosword.root.tip"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK, true, false, false
                ).criterion("root", TickCriterion.Conditions.createTick())
                .build(consumer, "root");

        Advancement card_pile = Advancement.Builder.create().parent(root)
                .display(ModItems.CARD_PILE,
                        Text.translatable("advancement.dabaosword.card_pile.title"),
                        Text.translatable("advancement.dabaosword.card_pile.tip"),
                        null,
                        AdvancementFrame.TASK, true, true, false
                ).criterion("card_pile", InventoryChangedCriterion.Conditions.items(ModItems.CARD_PILE))
                .build(consumer, "card_pile");

        Advancement gift_box = Advancement.Builder.create().parent(root)
                .display(ModItems.GIFTBOX,
                        Text.translatable("advancement.dabaosword.gift_box.title"),
                        Text.translatable("advancement.dabaosword.gift_box.tip"),
                        null,
                        AdvancementFrame.TASK, true, true, false
                ).criterion("gift_box", InventoryChangedCriterion.Conditions.items(ModItems.GIFTBOX))
                .build(consumer, "gift_box");
    }
}
