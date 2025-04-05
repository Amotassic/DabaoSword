package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.card.CardItem;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword:nahida")));
        loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword:card/gain_card")));
        var itemList = Registries.ITEM.stream().filter(item -> item instanceof CardItem).toList();
        for (var item : itemList) {
            String[] split = item.toString().split(":");
            String id = split[0] + ":card/" + split[1];
            loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(id)));
        }
    }
}
