package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.Card;
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

    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        addModel(new ModelIdentifier(new Identifier("dabaosword", "nahida"), "inventory"));
        addModel(new ModelIdentifier(new Identifier("dabaosword:card/gain_card"), "inventory"));
        var itemList = Registries.ITEM.stream().filter(item -> item instanceof Card).toList();
        for (var item : itemList) {
            String path = "card/" + item.toString();
            Identifier modelId = new Identifier("dabaosword", path);
            addModel(new ModelIdentifier(modelId, "inventory"));
        }
    }
}
