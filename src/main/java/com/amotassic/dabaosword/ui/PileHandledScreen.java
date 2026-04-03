package com.amotassic.dabaosword.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import org.jspecify.annotations.NonNull;

public class PileHandledScreen extends AbstractContainerScreen<PileScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

    public PileHandledScreen(PileScreenHandler handler, Inventory inventory, Component title) {
        int height = 114 + 4 * 18;
        super(handler, inventory, title, 176, height);
        this.inventoryLabelY = height - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float a) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0f, 0f, this.imageWidth, 4 * 18 + 17, 256, 256);
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j + 4 * 18 + 17, 0, 126, this.imageWidth, 96, 256, 256);
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
