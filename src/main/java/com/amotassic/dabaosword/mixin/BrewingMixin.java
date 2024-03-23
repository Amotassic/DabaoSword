package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingMixin {
    @Inject(method = "registerDefaults",at = @At("HEAD"))
    private static void register(CallbackInfo ci) {
        BrewingMixin.registerPotionRecipe(Potions.WATER, Items.WHEAT, ModItems.COOKING_WINE);
    }
    @Invoker("registerPotionRecipe")
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {}
}
