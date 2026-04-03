package com.amotassic.dabaosword.datagen;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RecipesProvider extends RecipeProvider {
    protected RecipesProvider(HolderLookup.Provider registries, RecipeOutput output) {super(registries, output);}

    @Override
    public void buildRecipes() {
        ItemStackTemplate smile = new ItemStackTemplate(ModItems.SUNSHINE_SMILE);
/*        var mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), 1);
        smile.apply(DataComponentPatch.builder().set(DataComponents.ENCHANTMENTS, mutable.toImmutable()).build());*/
        shapeless(RecipeCategory.COMBAT, smile)
                .requires(ModItems.GUDING_ITEM)
//                .input('X', ModItems.GUDING_ITEM)
//                .pattern("XXX")
//                .pattern("X X")
                .unlockedBy("has_guding", this.has(ModItems.GUDING_ITEM))
                .save(this.output);

    }

    public static class Provider extends FabricRecipeProvider {
        public Provider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput recipeOutput) {
            return new RecipesProvider(provider, recipeOutput);
        }

        @Override public @NonNull String getName() {return "dabaosword";}
    }
}
