package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.DabaoSword;
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
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.MenuScreens;

@SuppressWarnings("deprecation")
public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, TiesuoHud.TIESUO_HUD, TiesuoHud::onHudRender);
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, DabaoSword.id("change_skill_render"), ChangeSkillRender::onHudRender);
        MenuScreens.register(ModItems.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        MenuScreens.register(ModItems.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        MenuScreens.register(ModItems.PILE_SCREEN_HANDLER, PileHandledScreen::new);
        ClientTickEnd.initialize();
        ModelLayerRegistry.registerModelLayer(ModModelLayers.XUYOU, XuyouModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntity.XUYOU, XuyouRenderer::new);
        KeyInputCallback.KEY_INPUT.register(new KeyInputHandler());
    }
}
