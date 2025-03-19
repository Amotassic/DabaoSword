package com.amotassic.dabaosword.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.property.numeric.CustomModelDataFloatProperty;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {super(output);}

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {}

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
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

    private void registerCard(ItemModelGenerator generator, Item card) {
        ItemModelOutput output = generator.output;
        List<RangeDispatchItemModel.Entry> list = new ArrayList<>();
        ItemModel.Unbaked unbaked = ItemModels.basic(generator.registerSubModel(card, "", Models.GENERATED));
        list.add(ItemModels.rangeDispatchEntry(unbaked, 0.0F));

        for (int i = 1; i < 53; i++) {
            ItemModel.Unbaked unbaked2 = ItemModels.basic(generator.registerSubModel(card, "_" + i, Models.GENERATED));
            list.add(ItemModels.rangeDispatchEntry(unbaked2, (float) i));
        }

        output.accept(card, ItemModels.rangeDispatch(new CustomModelDataFloatProperty(0), 1.0F, list));
    }
}
