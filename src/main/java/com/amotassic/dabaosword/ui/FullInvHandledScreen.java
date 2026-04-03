package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class FullInvHandledScreen extends AbstractContainerScreen<FullInvScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final Set<Integer> slotsEnabled;
    private final int rows;
    private final boolean notSelf;
    private final int armorRow;
    private static final int TRINKET_INDEX = 43; //第一个饰品栏的索引

    public FullInvHandledScreen(FullInvScreenHandler handler, Inventory inventory, Component title) {
        int height = 38 + handler.rows * 18; if (handler.notSelf) height += 76;
        super(handler, inventory, title, 176, height);
        this.slotsEnabled = handler.slotsEnabled;
        this.rows = handler.rows;
        this.notSelf = handler.notSelf;
        this.armorRow = handler.armorRow;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float a) {
        int x = this.leftPos; int y = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,0,0, imageWidth, 17, 256, 256);
        for (int i = 0; i < rows * 2; i++) { //填充空白背景
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 17 + i * 9,0,125, imageWidth, 9, 256, 256);
        }
        int v = notSelf ? 125 : 215; int height = notSelf ? 97 : 7;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + rows * 18 + 17,0, v, imageWidth, height, 256, 256);
        var trinkets = ModTools.trinketsWithSlots(menu.target);
        for (int i : slotsEnabled) { //绘制启用的格子背景
            Slot slot = menu.getSlot(i);
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + x - 1, slot.y + y - 1,7, 17, 18, 18, 256, 256);

            int index = i - TRINKET_INDEX; // 绘制饰品槽位图标
            if (index < 0 || slot.hasItem()) continue;
            var texture = trinkets.get(index).getA().getSlotType().getIcon();
            if (texture == null) continue;
            context.blit(RenderPipelines.GUI_TEXTURED, texture, slot.x + x, slot.y + y, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor context, int xm, int ym) {
        context.text(this.font, this.title, this.titleLabelX, this.titleLabelY, CommonColors.DARK_GRAY, false);
        if (notSelf) context.text(this.font, this.playerInventoryTitle, 8, 21 + rows * 18, CommonColors.DARK_GRAY, false);
        if (slotsEnabled.contains(TRINKET_INDEX)) context.text(this.font, Component.translatable("trinkets"), 8, 5 + armorRow * 18, CommonColors.DARK_GRAY, false);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent input) {
        if (minecraft.gameMode != null && hoveredSlot != null && minecraft.player != null && input.key() == 261) {
            minecraft.gameMode.handleContainerInput(menu.containerId, hoveredSlot.index, 114, ContainerInput.THROW, minecraft.player);
            return true;
        }
        return super.keyPressed(input);
    }
}
