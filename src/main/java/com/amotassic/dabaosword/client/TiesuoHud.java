package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

@Environment(value= EnvType.CLIENT)
public class TiesuoHud {
    public static final Identifier TIESUO_HUD = DabaoSword.id("textures/misc/tiesuo_hud.png");
    private static float tiesuoScale;

    public static void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        var client = Minecraft.getInstance();
        var player = client.player;
        if (player == null || !client.options.getCameraType().isFirstPerson()) return;
        if (player.isUsingItem() && player.getActiveItem().is(ModItems.TIESUO) && player.getOffhandItem().is(Items.KNOWLEDGE_BOOK)) {
            float f = tickCounter.getGameTimeDeltaTicks();
            tiesuoScale = Mth.lerp(0.5f * f, tiesuoScale, 1.125f);
            renderTiesuoOverlay(drawContext, tiesuoScale);
        }
    }

    private static void renderTiesuoOverlay(GuiGraphicsExtractor context, float scale) {
        int width = context.guiWidth(); int height = context.guiHeight();
        float f;
        float g = f = (float)Math.max(width, height);
        float h = Math.max((float) width / f, (float) height / g) * scale;
        int i = Mth.floor(f * h);
        int j = Mth.floor(g * h);
        int k = (width - i) / 2;
        int l = (height - j) / 2;
        int m = k + i;
        int n = l + j;
        context.blit(RenderPipelines.GUI_TEXTURED, TIESUO_HUD, k, l, 0.0f, 0.0f, i, j, i, j);
        context.fill(RenderPipelines.GUI, 0, n, width, height, -16777216);
        context.fill(RenderPipelines.GUI, 0, 0, width, l, -16777216);
        context.fill(RenderPipelines.GUI, 0, l, k, n, -16777216);
        context.fill(RenderPipelines.GUI, m, l, width, n, -16777216);
    }
}
