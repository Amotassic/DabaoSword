package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Set;

public class FullInvHandledScreen extends HandledScreen<FullInvScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("textures/gui/container/generic_54.png");
    private final Set<Integer> slotsEnabled;
    private final int rows;
    private final boolean notSelf;
    private final int armorRow;
    private static final int TRINKET_INDEX = 43; //第一个饰品栏的索引

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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,0,0, backgroundWidth, 17, 256, 256);
        for (int i = 0; i < rows * 2; i++) { //填充空白背景
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 17 + i * 9,0,125, backgroundWidth, 9, 256, 256);
        }
        int v = notSelf ? 125 : 215; int height = notSelf ? 97 : 7;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + rows * 18 + 17,0, v, backgroundWidth, height, 256, 256);
        var trinkets = ModTools.trinketsWithSlots(handler.target);
        for (int i : slotsEnabled) { //绘制启用的格子背景
            Slot slot = handler.getSlot(i);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + x - 1, slot.y + y - 1,7, 17, 18, 18, 256, 256);

            int index = i - TRINKET_INDEX; // 绘制饰品槽位图标
            if (index < 0 || slot.hasStack()) continue;
            var texture = trinkets.get(index).getLeft().getSlotType().getIcon();
            if (texture == null) continue;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, slot.x + x, slot.y + y, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, Colors.DARK_GRAY, false);
        if (notSelf) context.drawText(this.textRenderer, this.playerInventoryTitle, 8, 21 + rows * 18, Colors.DARK_GRAY, false);
        if (slotsEnabled.contains(TRINKET_INDEX)) context.drawText(this.textRenderer, Text.translatable("trinkets"), 8, 5 + armorRow * 18, Colors.DARK_GRAY, false);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (client != null && client.interactionManager != null && focusedSlot!= null && input.key() == 261) {
            client.interactionManager.clickSlot(handler.syncId, focusedSlot.id, 114, SlotActionType.THROW, client.player);
            return true;
        }
        return super.keyPressed(input);
    }
}
