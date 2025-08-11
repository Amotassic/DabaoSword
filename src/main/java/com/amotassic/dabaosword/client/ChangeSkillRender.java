package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.network.QuickSwapPayload;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ChangeSkillRender implements HudRenderCallback {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static boolean isRendering = false;
    private static int screenWidth;
    private static int screenHeight;
    private static int centerX;
    private static int centerY;
    private static List<ItemStack> skills = new ArrayList<>();

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        ClientPlayerEntity player = client.player;
        if (!isRendering || player == null) return;
        if (skills.isEmpty()) skills = ModTools.trinketsWithSlots(player, s -> s.isIn(Tags.SKILLS)).stream().map(p -> p.getLeft().getStack(p.getRight())).toList();
        if (skills.size() < 2) {
            close();
            client.mouse.lockCursor();
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        screenWidth = client.getWindow().getScaledWidth();
        screenHeight = client.getWindow().getScaledHeight();
        centerX = screenWidth / 2;
        centerY = screenHeight / 2;
        int radius = 80; // 离原点的半径
        int n = skills.size();

        for (int i = 0; i < n; i++) {
            // 计算每个扇形的角度（弧度），以竖直向上为0°，顺时针为正
            float angle = (float) (2 * Math.PI * i / n - Math.PI / 2);
            // 计算文本坐标（注意Minecraft的Y轴向下为正）
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            // 绘制文本（x,y为文本左上角坐标，需调整以居中显示）
            ItemStack skill = skills.get(i);
            OrderedText text = skill.toHoverableText().asOrderedText();
            drawContext.drawItem(skill, x - 8, y - 16);
            drawContext.drawText(textRenderer, text, x - textRenderer.getWidth(text) / 2, y, -1, false);
        }
    }

    public static void close() {
        isRendering = false;
        skills = new ArrayList<>();
    }

    public static void handleSkillSelect(double x, double y) {
        if (!isRendering) return;

        double angleDegrees = getAngleDegrees(x, y);
        int parts = skills.size();
        double partAngle = 360.0 / parts;
        double fixedDegree = angleDegrees + partAngle / 2;
        if (fixedDegree >= 360) fixedDegree -= 360;
        int part = (int) (fixedDegree / partAngle);
        close();
        if (part <= 0) return;

        // System.out.printf("鼠标与中心夹角: %.2f°，所在部分: %d\n", angleDegrees, part);
        ClientPlayNetworking.send(new QuickSwapPayload(part + 100));
    }

    private static double getAngleDegrees(double x, double y) {
        var clientWindow = client.getWindow();
        double d = x * (double) screenWidth / (double) clientWindow.getWidth();
        double e = y * (double) screenHeight / (double) clientWindow.getHeight();
        // 计算鼠标相对于中心的偏移量（注意y轴方向需要反转，因为屏幕坐标向下为正）
        double offsetX = d - centerX;
        double offsetY = centerY - e;  // 反转Y轴，使向上为正方向
        // 计算夹角（弧度制），使用atan2(dx, dy)确保0°在竖直向上方向
        double angleRadians = Math.atan2(offsetX, offsetY);
        // 转换为角度制
        double angleDegrees = Math.toDegrees(angleRadians);
        // 确保角度在0-360°范围内
        if (angleDegrees < 0) angleDegrees += 360;
        return angleDegrees;
    }
}
