package com.amotassic.dabaosword.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static com.amotassic.dabaosword.items.ModItems.TIESUO;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        //自定义谓词，用于改变铁索连环的纹理
        ModelPredicateProviderRegistry.register(TIESUO, new Identifier("nahida"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK ? 1.0F : 0.0F;
        });
    }
}
