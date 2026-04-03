package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amotassic.dabaosword.util.ModTools.getOrCreateNbt;
import static com.amotassic.dabaosword.util.ModTools.s;

public class PlayerInvHandledScreen extends AbstractContainerScreen<PlayerInvScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final int rows;

    public PlayerInvHandledScreen(PlayerInvScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 24 + handler.rows * 18);
        this.rows = handler.rows;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float a) {
        //renderBg(graphics, a, mouseX, mouseY); //不要加深背景
        int x = this.leftPos; int y = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,0,0, imageWidth, 17, 256, 256);
        for (int i = 0; i < rows; i++) {
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 17 + i * 18,0,17, imageWidth, 18, 256, 256);
        }
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + rows * 18 + 17,0,215, imageWidth, 7, 256, 256);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float a) {
        List<Component> screenTips = new ArrayList<>();
        var skill = s(eventStack());
        skill.item.addScreenTip(skill, screenTips);
        if (skill.stack.isEmpty()) {
            screenTips.clear();
            screenTips.add(Component.translatable("screen.dabaosword.select_card"));
        }
        if (!screenTips.isEmpty()) for (var text : screenTips) {
            int y = 2 + 10 * screenTips.indexOf(text);
            int textWidth = font.width(text);
            // 绘制文本背景
            context.fill(1, y - 1, 1 + textWidth + 2, y + font.lineHeight, 0xFF202020);
            // 绘制文本
            context.text(font, text, 2, y, -1, false);
        }
        super.extractRenderState(context, mouseX, mouseY, a);
        //1.21.6之后不得不这样改，暂时想不到更好的办法
        getClicks().forEach((i, clicks) -> {
            Slot slot = menu.getSlot(i);
            context.text(this.font, Component.literal(clicks + "").withStyle(ChatFormatting.RED), slot.x + leftPos, slot.y + 6 + topPos, CommonColors.DARK_GRAY, false);
        });
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, CommonColors.DARK_GRAY, false);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int keyCode = input.key();
        int modifiers = input.modifiers();
        //System.out.println("keyCode: " + keyCode + " scanCode: " + scanCode + " modifiers: " + modifiers);
        boolean ctrlA = keyCode == 65 && modifiers == 2;
        boolean ctrlZ = keyCode == 90 && modifiers == 2;
        var stack = eventStack();
        var manager = minecraft.gameMode; var player = minecraft.player;
        if (manager != null && player != null) {
            if (ctrlA) {manager.handleContainerInput(menu.containerId, 0, 65, ContainerInput.PICKUP_ALL, player); return true;}
            if (ctrlZ) {manager.handleContainerInput(menu.containerId, 0, 90, ContainerInput.PICKUP_ALL, player); return true;}
        }
        boolean canClose = !(stack.getItem() instanceof ISkill) || selectedCount() >= s(stack).getMinSelect();
        if (!canClose || stack.is(ModItems.DISCARD) || stack.is(ModItems.STEAL)) {
            if (minecraft.options.keyInventory.matches(input) || keyCode == 256) return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        var manager = minecraft.gameMode; var player = minecraft.player;
        if (manager != null && hoveredSlot != null && player != null) {
            int id = hoveredSlot.index; //仅用于根据鼠标滚轮滚动来选择卡牌
            if (amount == 1.0) manager.handleContainerInput(menu.containerId, id, 0, ContainerInput.PICKUP, player);
            if (amount == -1.0) manager.handleContainerInput(menu.containerId, id, 1, ContainerInput.PICKUP, player);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, amount);
    }

    private ItemStack eventStack() {return menu.getSlot(81).getItem();}

    private Map<Integer, Integer> getClicks() {
        String str = getOrCreateNbt(menu.getSlot(82).getItem()).getString("Clicks").orElse("");
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
