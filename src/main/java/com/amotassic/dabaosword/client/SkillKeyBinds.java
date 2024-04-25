package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.network.ServerNetworking;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class SkillKeyBinds {
    private static final KeyBinding ACTIVE_SKILL = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.active_skill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.dabaosword.keybindings"));

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ACTIVE_SKILL.wasPressed()) {
                ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, new PacketByteBuf(Unpooled.buffer()));
            }
        });
    }
}
