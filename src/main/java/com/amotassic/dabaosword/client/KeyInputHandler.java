package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler implements KeyInputCallback {
    private String keysPressed = "";
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void onKeyInput(int key, int scancode, int action, int modifiers) {
        //System.out.println("key: " + key + " scancode: " + scancode + " action: " + action + "mod: " + modifiers);
        if (action == 1 && modifiers == 2 && mc.player != null) {
            if (key == GLFW.GLFW_KEY_M) mc.player.sendSystemMessage(DabaoSwordCommand.menu);
            if (key == GLFW.GLFW_KEY_I) SimplePayload.sendToServer(SimplePayload.VIEW_INFO);
            return;
        }

        var skillKey = KeyMappingHelper.getBoundKeyOf(ClientTickEnd.ACTIVE_SKILL).getValue();
        var user = mc.player;
        if (key == skillKey && user != null && mc.screen == null) {
            // 短按（松开）发动主动技能
            if (action == 0 && !ChangeSkillRender.isRendering && ModTools.isEquipped(user, s -> ModTools.s(s).isActiveSkill())) {
                var result = mc.hitResult; LivingEntity target;
                if (result instanceof EntityHitResult eResult && eResult.getEntity() instanceof LivingEntity entity) {
                    target = entity;
                } else target = user;

                SimplePayload.sendToServer(SimplePayload.ACTIVE_SKILL, Integer.toString(target.getId()));
            } // 长按打开技能选择轮盘
            if (action == 2) {
                ChangeSkillRender.isRendering = true;
                mc.mouseHandler.releaseMouse();
            }
            return;
        }

        if (action != GLFW.GLFW_PRESS) return;

        String keyPressed = getInputKey(key);
        if (keyPressed.isEmpty()) {
            keysPressed = "";
            return;
        }
        keysPressed += keyPressed;
        String MURASAME = "MURASAME";
        if (!MURASAME.startsWith(keysPressed)) keysPressed = "";
        if (MURASAME.equals(keysPressed)) {
            doSomething();
            keysPressed = "";
        }
    }

    private void doSomething() {
        var player = mc.player;
        if (player == null) return;
        player.sendSystemMessage(Component.literal("MURASAME"));
    }

    private String getInputKey(int key) {
        if (key >= 48 && key <= 57) return String.valueOf(key - 48);
        if (key >= 65 && key <= 90) return String.valueOf((char) key);
        return "";
    }
}
