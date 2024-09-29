package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.FullInvHandledScreen;
import com.amotassic.dabaosword.ui.PlayerInvHandledScreen;
import com.amotassic.dabaosword.ui.SimpleMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HandledScreens.register(ModItems.SIMPLE_MENU_HANDLER, SimpleMenuScreen::new);
        HandledScreens.register(ModItems.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        HandledScreens.register(ModItems.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        ClientTickEnd.initialize();

        //自定义谓词，用于改变铁索连环的纹理
        ModelPredicateProviderRegistry.register(ModItems.TIESUO, Identifier.of("nahida"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) return 0.0F;
            return livingEntity.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK ? 1.0F : 0.0F;
        });
    }
}
