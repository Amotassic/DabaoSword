package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class CardPileHud implements HudRenderCallback {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private int x = 10;
    private int y = 10;
    public static boolean isRendering = false;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        var pack = ModTools.getCardPack(client.player);
        if (pack == null || pack.isEmpty()) {
            isRendering = false;
            return;
        }

        isRendering = true;
        int actualX = x * client.getWindow().getScaledWidth() / 100;
        int actualY = y * client.getWindow().getScaledHeight() / 100;
        for (int i = 0; i < pack.cards.size(); i++) {
            var card = pack.cards.get(i);
            if (card.isEmpty()) continue;
            drawContext.drawItem(card, actualX + (i % 9) * 18, actualY + i / 9 * 18);
            drawContext.drawItemInSlot(client.textRenderer, card, actualX + (i % 9) * 18, actualY + i / 9 * 18);
        }
    }
}
