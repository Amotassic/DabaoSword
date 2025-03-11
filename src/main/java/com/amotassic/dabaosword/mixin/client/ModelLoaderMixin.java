package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.Card;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ReferencedModelsCollector.class)
public abstract class ModelLoaderMixin {

    @Inject(method = "getRequiredModels", at = @At("TAIL"), cancellable = true)
    private static void init(CallbackInfoReturnable<Set<ModelIdentifier>> cir) {
        var set = cir.getReturnValue();
        set.add(ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword:nahida")));
        set.add(ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword:card/gain_card")));
        var itemList = Registries.ITEM.stream().filter(item -> item instanceof Card).toList();
        for (var item : itemList) {
            String[] split = item.toString().split(":");
            String id = split[0] + ":card/" + split[1];
            set.add(ModelIdentifier.ofInventoryVariant(Identifier.of(id)));
        }
        cir.setReturnValue(set);
    }
}
