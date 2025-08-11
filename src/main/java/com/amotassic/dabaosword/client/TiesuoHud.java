package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class TiesuoHud {

    private static final Identifier TIESUO_HUD = Identifier.of("dabaosword","textures/misc/tiesuo_hud.png");

    private static float tiesuoScale;

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, TiesuoHud.TIESUO_HUD, TiesuoHud::onHudRender);
    }

    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        int scaledWidth = drawContext.getScaledWindowWidth();
        int scaledHeight = drawContext.getScaledWindowHeight();
        float f = tickCounter.getDynamicDeltaTicks();
        tiesuoScale = MathHelper.lerp(0.5f * f, tiesuoScale, 1.125f);
        if (client.options.getPerspective().isFirstPerson()) {
            var player = client.player;
            if (player != null && player.isUsingItem() && player.getActiveItem().isOf(ModItems.TIESUO) && player.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
                renderTiesuoOverlay(drawContext, tiesuoScale, scaledWidth, scaledHeight);
            }
        }
    }

    private static void renderTiesuoOverlay(DrawContext context, float scale, int width, int height) {
        float f;
        float g = f = (float)Math.max(width, height);
        float h = Math.max((float)width / f, (float) height / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (width - i) / 2;
        int l = (height - j) / 2;
        int m = k + i;
        int n = l + j;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TIESUO_HUD, k, l, 0.0f, 0.0f, i, j, i, j);
        context.fill(RenderPipelines.GUI, 0, n, width, height, -16777216);
        context.fill(RenderPipelines.GUI, 0, 0, width, l, -16777216);
        context.fill(RenderPipelines.GUI, 0, l, k, n, -16777216);
        context.fill(RenderPipelines.GUI, m, l, width, n, -16777216);
    }
}
