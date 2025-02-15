package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.network.ServerNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler implements KeyInputCallback {
    private String keysPressed = "";
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onKeyInput(int key, int scancode, int action, int modifiers) {
        //System.out.println("key: " + key + " scancode: " + scancode + " action: " + action + "mod: " + modifiers);
        if (action == 1 && modifiers == 2 && mc.player != null) {
            if (key == GLFW.GLFW_KEY_M) mc.player.sendMessage(DabaoSwordCommand.menu);
            if (key == GLFW.GLFW_KEY_I) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(9);
                ClientPlayNetworking.send(ServerNetworking.SELECT_CARD, buf);
            }
            return;
        }
        if (action != GLFW.GLFW_PRESS) return;

        String keyPressed = getInputKey(key);
        if (keyPressed.isEmpty()) {
            keysPressed = "";
            return;
        }
        keysPressed = keysPressed + keyPressed;
        String MURASAME = "MURASAME";
        if (!MURASAME.substring(0, keysPressed.length()).equals(keysPressed)) keysPressed = "";
        if (MURASAME.equals(keysPressed)) {
            doSomething();
            keysPressed = "";
        }
    }

    private void doSomething() {
        var player = mc.player;
        if (player == null) return;
        player.sendMessage(Text.of("MURASAME"));
    }

    private String getInputKey(int key) {
        if (key >= 48 && key <= 57) return String.valueOf(key - 48);
        if (key >= 65 && key <= 90) return String.valueOf((char) key);
        return "";
    }
}
