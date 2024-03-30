package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class TiesuoHud implements HudRenderCallback {

    private static final Identifier TIESUO_HUD = new Identifier("dabaosword","textures/misc/tiesuo_hud.png");

    private int scaledWidth;
    private int scaledHeight;
    private float tiesuoScale;

    public TiesuoHud() {}

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.scaledWidth = drawContext.getScaledWindowWidth();
        this.scaledHeight = drawContext.getScaledWindowHeight();
        float f = client.getLastFrameDuration();
        this.tiesuoScale = MathHelper.lerp(0.5f * f, this.tiesuoScale, 1.125f);
        if (client.options.getPerspective().isFirstPerson()) {
            if (client.player != null && client.player.isUsingItem() && client.player.getActiveItem().isOf(ModItems.TIESUO) && client.player.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
                this.renderTiesuoOverlay(drawContext, this.tiesuoScale);
            }
        }
    }

    private void renderTiesuoOverlay(DrawContext context, float scale) {
        float f;
        float g = f = (float)Math.max(this.scaledWidth, this.scaledHeight);
        float h = Math.max((float)this.scaledWidth / f, (float)this.scaledHeight / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (this.scaledWidth - i) / 2;
        int l = (this.scaledHeight - j) / 2;
        int m = k + i;
        int n = l + j;
        context.drawTexture(TIESUO_HUD, k, l, -90, 0.0f, 0.0f, i, j, i, j);
        context.fill(RenderLayer.getGuiOverlay(), 0, n, this.scaledWidth, this.scaledHeight, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.scaledWidth, l, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), m, l, this.scaledWidth, n, -90, -16777216);
    }
}
