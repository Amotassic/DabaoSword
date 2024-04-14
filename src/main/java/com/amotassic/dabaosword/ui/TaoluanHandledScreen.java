package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TaoluanHandledScreen extends HandledScreen<TaoluanScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("dabaosword", "textures/gui/taoluan.png");

    public TaoluanHandledScreen(TaoluanScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 58;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.x; int y = this.y;
        context.drawTexture(TEXTURE, x, y,0,0, backgroundWidth, backgroundHeight);
        context.drawItem(ModItems.SHAN.getDefaultStack(),x + 8,y+16);
        context.drawItem(ModItems.PEACH.getDefaultStack(),x+8+ 18,y+16);
        context.drawItem(ModItems.JIU.getDefaultStack(),x+8+18*2,y+16);
        context.drawItem(ModItems.BINGLIANG_ITEM.getDefaultStack(),x+8+18*3,y+16);
        context.drawItem(ModItems.TOO_HAPPY_ITEM.getDefaultStack(),x+8+18*4,y+16);
        context.drawItem(ModItems.DISCARD.getDefaultStack(),x+8+18*5,y+16);
        context.drawItem(ModItems.FIRE_ATTACK.getDefaultStack(),x+8+18*6,y+16);
        context.drawItem(ModItems.JIEDAO.getDefaultStack(),x+8+18*7,y+16);
        context.drawItem(ModItems.JUEDOU.getDefaultStack(),x+8+18*8,y+16);
        context.drawItem(ModItems.NANMAN.getDefaultStack(),x + 8,y+34);
        context.drawItem(ModItems.STEAL.getDefaultStack(),x+8+ 18,y+34);
        context.drawItem(ModItems.TIESUO.getDefaultStack(),x+8+18*2,y+34);
        context.drawItem(ModItems.WUXIE.getDefaultStack(),x+8+18*3,y+34);
        context.drawItem(ModItems.WUZHONG.getDefaultStack(),x+8+18*4,y+34);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
