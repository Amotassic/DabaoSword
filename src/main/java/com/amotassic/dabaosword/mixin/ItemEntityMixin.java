package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void resetPickupDelay();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ModTools.isCard(this.getStack())) this.resetPickupDelay();
    }
}
