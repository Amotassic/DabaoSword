package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.datagen.ModModelProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangeSelectItemModelProperties.class)
public class RangeSelectMixin {

    @Shadow @Final
    public static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At(value = "HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        ID_MAPPER.put(DabaoSword.id("card_sr"), ModModelProvider.CardSR.MAP_CODEC);
    }
}
