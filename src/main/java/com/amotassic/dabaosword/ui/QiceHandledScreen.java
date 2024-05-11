package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class QiceHandledScreen extends HandledScreen<QiceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("dabaosword", "textures/gui/taoluan.png");

    public QiceHandledScreen(QiceScreenHandler handler, PlayerInventory inventory, Text title) {
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

        Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUZHONG};
        // 定义物品起始绘制位置
        int itemX = x + 8;
        int itemY = y + 16;
        // 遍历物品数组，绘制物品图标
        for (Item item : items) {
            context.drawItem(item.getDefaultStack(), itemX, itemY);
            // 更新物品绘制位置
            itemX += 18;
            // 如果一行绘制完毕，则换行
            if (itemX > x + 8 + 18 * 8) {
                itemX = x + 8;
                itemY += 18;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}
