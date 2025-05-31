package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amotassic.dabaosword.util.ModTools.getOrCreateNbt;
import static com.amotassic.dabaosword.util.ModTools.s;

public class PlayerInvHandledScreen extends HandledScreen<PlayerInvScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("textures/gui/container/generic_54.png");
    private final int rows;

    public PlayerInvHandledScreen(PlayerInvScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.rows = handler.rows;
        this.backgroundHeight = 24 + rows * 18;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        drawBackground(context, delta, mouseX, mouseY); //不要加深背景
    }
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y,0,0, backgroundWidth, 17, 256, 256);
        for (int i = 0; i < rows; i++) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y + 17 + i * 18,0,17, backgroundWidth, 18, 256, 256);
        }
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y + rows * 18 + 17,0,215, backgroundWidth, 7, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<Text> screenTips = new ArrayList<>();
        var skill = s(eventStack());
        skill.item.addScreenTip(skill, screenTips);
        if (!screenTips.isEmpty()) for (var text : screenTips) {
            int y = 2 + 10 * screenTips.indexOf(text);
            int textWidth = textRenderer.getWidth(text);
            // 绘制文本背景
            context.fill(1, y - 1, 1 + textWidth + 2, y + textRenderer.fontHeight, 0xFF202020);
            // 绘制文本
            context.drawText(textRenderer, text, 2, y, 0xE0E0E0, false);
        }
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0f, 0f, 400f);
        getClicks().forEach((i, clicks) -> {
            Slot slot = handler.getSlot(i);
            context.drawText(this.textRenderer, Text.literal(clicks + "").formatted(Formatting.RED), slot.x, slot.y + 6, 0x404040, false);
        });
        matrices.pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //System.out.println("keyCode: " + keyCode + " scanCode: " + scanCode + " modifiers: " + modifiers);
        boolean ctrlA = keyCode == 65 && modifiers == 2;
        boolean ctrlZ = keyCode == 90 && modifiers == 2;
        assert client != null; var stack = eventStack();
        var manager = client.interactionManager;
        if (manager != null) {
            if (ctrlA) {manager.clickSlot(handler.syncId, 0, 65, SlotActionType.PICKUP_ALL, client.player); return true;}
            if (ctrlZ) {manager.clickSlot(handler.syncId, 0, 90, SlotActionType.PICKUP_ALL, client.player); return true;}
        }
        boolean canClose = !(stack.getItem() instanceof ISkill) || selectedCount() >= s(stack).getMinSelect();
        if (!canClose || stack.isOf(ModItems.DISCARD) || stack.isOf(ModItems.STEAL)) {
            if (client.options.inventoryKey.matchesKey(keyCode, scanCode) || keyCode == 256) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        if (client == null) return false;
        var manager = client.interactionManager;
        if (manager != null && focusedSlot != null) {
            int id = focusedSlot.id; //仅用于根据鼠标滚轮滚动来选择卡牌
            if (amount == 1.0) manager.clickSlot(handler.syncId, id, 0, SlotActionType.PICKUP, client.player);
            if (amount == -1.0) manager.clickSlot(handler.syncId, id, 1, SlotActionType.PICKUP, client.player);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    private ItemStack eventStack() {return handler.getSlot(81).getStack();}

    private Map<Integer, Integer> getClicks() {
        String str = getOrCreateNbt(handler.getSlot(82).getStack()).getString("Clicks").orElse("");
        Map<Integer, Integer> clicks = new HashMap<>();
        if (str.isEmpty()) return clicks;
        str = str.substring(1, str.length() - 1); //去掉{}如果还是空，则返回空map
        if (str.isEmpty()) return clicks;

        String[] split = str.split(", ");
        for (String s : split) {
            String[] split1 = s.split("=");
            clicks.put(Integer.parseInt(split1[0]), Integer.parseInt(split1[1]));
        }
        return clicks;
    }
    private int selectedCount() {return getClicks().values().stream().mapToInt(Integer::intValue).sum();}
}
