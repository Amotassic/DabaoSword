package com.amotassic.dabaosword.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Set;

public class FullInvHandledScreen extends HandledScreen<FullInvScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("textures/gui/container/generic_54.png");
    private final Set<Integer> slotsEnabled;
    private final int rows;
    private final boolean notSelf;
    private final int armorRow;

    public FullInvHandledScreen(FullInvScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.slotsEnabled = handler.slotsEnabled;
        this.rows = handler.rows;
        this.notSelf = handler.notSelf;
        this.armorRow = handler.armorRow;
        int height = 38 + rows * 18; if (notSelf) height += 76;
        this.backgroundHeight = height;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y,0,0, backgroundWidth, 17, 256, 256);
        for (int i = 0; i < rows * 2; i++) { //填充空白背景
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y + 17 + i * 9,0,125, backgroundWidth, 9, 256, 256);
        }
        int v = notSelf ? 125 : 215; int height = notSelf ? 97 : 7;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y + rows * 18 + 17,0, v, backgroundWidth, height, 256, 256);
        for (int i : slotsEnabled) { //绘制启用的格子背景
            Slot slot = handler.getSlot(i);
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, slot.x + x - 1, slot.y + y - 1,7, 17, 18, 18, 256, 256);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        if (notSelf) context.drawText(this.textRenderer, this.playerInventoryTitle, 8, 21 + rows * 18, 0x404040, false);
        if (slotsEnabled.contains(41)) context.drawText(this.textRenderer, Text.translatable("trinkets"), 8, 5 + armorRow * 18, 0x404040, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (client != null && client.interactionManager != null && focusedSlot!= null && keyCode == 261) {
            client.interactionManager.clickSlot(handler.syncId, focusedSlot.id, 114, SlotActionType.THROW, client.player);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
