package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import com.amotassic.dabaosword.entity.ModEntity;
import com.amotassic.dabaosword.entity.client.ModModelLayers;
import com.amotassic.dabaosword.entity.client.XuyouModel;
import com.amotassic.dabaosword.entity.client.XuyouRenderer;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.FullInvHandledScreen;
import com.amotassic.dabaosword.ui.PileHandledScreen;
import com.amotassic.dabaosword.ui.PlayerInvHandledScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HandledScreens.register(ModItems.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        HandledScreens.register(ModItems.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        HandledScreens.register(ModItems.PILE_SCREEN_HANDLER, PileHandledScreen::new);
        ClientTickEnd.initialize();
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.XUYOU, XuyouModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntity.XUYOU, XuyouRenderer::new);
        KeyInputCallback.KEY_INPUT.register(new KeyInputHandler());
    }
}
