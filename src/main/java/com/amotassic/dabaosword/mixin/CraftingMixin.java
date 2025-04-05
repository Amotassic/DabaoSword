package com.amotassic.dabaosword.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.CrafterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.amotassic.dabaosword.util.ModifyDamage.modifyStack;

@Mixin(CraftingScreenHandler.class)
abstract class CraftingMixin {

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private static void updateResult(ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci, @Local(ordinal = 0) ItemStack stack) {
        modifyStack(stack);
    }
}

@Mixin(CrafterScreenHandler.class)
abstract class CrafterScreen {

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private void updateResult(CallbackInfo ci, @Local(ordinal = 0) ItemStack stack) {
        modifyStack(stack);
    }
}

@Mixin(CrafterBlock.class)
abstract class Crafter {

    @ModifyVariable(method = "transferOrSpawnStack", at = @At("HEAD"), argsOnly = true)
    private ItemStack transferOrSpawnStack(ItemStack stack) {
        return modifyStack(stack);
    }
}