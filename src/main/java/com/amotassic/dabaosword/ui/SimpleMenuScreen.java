package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SimpleMenuScreen extends HandledScreen<SimpleMenuHandler> {
    private static final Identifier TEXTURE = Identifier.of("dabaosword", "textures/gui/menu_18.png");

    public SimpleMenuScreen(SimpleMenuHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 58;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x; int y = this.y;
        if (handler.slots.get(18).getStack().isOf(SkillCards.JIZHAN)) context.drawTexture(TEXTURE, x, y,0,75, backgroundWidth, backgroundHeight);
        else context.drawTexture(TEXTURE, x, y,0,0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
    }

    @Override @SuppressWarnings("all")
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ItemStack stack = handler.slots.get(18).getStack();
        boolean canClose = !(stack.getItem() instanceof Skill skill) || skill.canCloseGUI(stack);
        if (!canClose) {
            if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode) || keyCode == 256) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
