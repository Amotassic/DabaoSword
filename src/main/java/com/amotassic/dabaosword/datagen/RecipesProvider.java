package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipesProvider extends RecipeGenerator {
    public RecipesProvider(RegistryWrapper.WrapperLookup lookup, RecipeExporter exporter) {
        super(lookup, exporter);
    }

    @Override
    public void generate() {
        ItemStack smile = new ItemStack(ModItems.SUNSHINE_SMILE);
        smile.addEnchantment(registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), 1);
        createShapeless(RecipeCategory.COMBAT, smile)
                .input(ModItems.GUDING_ITEM)
//                .input('X', ModItems.GUDING_ITEM)
//                .pattern("XXX")
//                .pattern("X X")
                .criterion("has_guding", this.conditionsFromItem(ModItems.GUDING_ITEM))
                .offerTo(this.exporter);
    }

    public static class Provider extends FabricRecipeProvider {
        public Provider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
            return new RecipesProvider(wrapperLookup, recipeExporter);
        }

        @Override public String getName() {return "dabaosword";}
    }
}
