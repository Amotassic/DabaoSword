package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {super(output);}

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {}

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators generator) {
/*        generator.register(ModItems.GAIN_CARD);
        generator.register(ModItems.CARD_PILE);
        generator.register(ModItems.GIFTBOX);
        generator.register(ModItems.GUDINGDAO);
        generator.register(ModItems.ARROW_RAIN);
        generator.register(ModItems.BBJI);
        generator.register(ModItems.LET_ME_CC);
        generator.register(ModItems.SUNSHINE_SMILE);
        generator.registerSpawnEgg(ModItems.XUYOU_SPAWN_EGG, 0x52BDF7, 0x8D8B96);
        generator.register(ModItems.GUDING_ITEM);
        generator.register(ModItems.INCOMPLETE_GUDINGDAO);

        for (Item item : SkillCards.SKILLS) generator.register(item);*/

        //for (Item item : ModItems.CARDS) registerCard(generator, item);
    }

    private void registerCard(ItemModelGenerators generator, Item card) {
        ItemModelOutput output = generator.itemModelOutput;
        List<RangeSelectItemModel.Entry> list = new ArrayList<>();
        ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(generator.createFlatItemModel(card, "", ModelTemplates.FLAT_ITEM));
        list.add(ItemModelUtils.override(unbaked, 0.0F));

        for (int i = 1; i < 57; i++) {
            ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(generator.createFlatItemModel(card, "_" + i, ModelTemplates.FLAT_ITEM));
            list.add(ItemModelUtils.override(unbaked2, (float) i));
        }

        ItemModel.Unbaked fallback = ItemModelUtils.rangeSelect(new CardSR(), unbaked, list);
        ItemModel.Unbaked select = ItemModelUtils.select(new DisplayContext(), fallback, ItemModelUtils.when(ItemDisplayContext.GUI, unbaked));

        output.accept(card, select);
    }

    public record CardSR() implements RangeSelectItemModelProperty {
        public static final MapCodec<CardSR> MAP_CODEC = MapCodec.unit(new CardSR());

        @Override
        public float get(@NonNull ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            Card card = ModTools.c(stack);
            if (card.suit != Suit.None) {
                if (card.rank != Rank.None) {
                    int s = card.suit.ordinal();
                    int r = card.rank.ordinal() + 1;
                    return 13 * s + r;
                } else return 53 + card.suit.ordinal();
            } else return 0;
        }

        @Override
        public @NonNull MapCodec<? extends RangeSelectItemModelProperty> type() {return MAP_CODEC;}
    }
}
