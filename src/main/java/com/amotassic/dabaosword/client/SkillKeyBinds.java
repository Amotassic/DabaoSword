package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.network.ServerNetworking;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static com.amotassic.dabaosword.util.ModTools.hasTrinkets;

public class SkillKeyBinds {
    private static final KeyBinding ACTIVE_SKILL = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.active_skill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.dabaosword.keybindings"));

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ACTIVE_SKILL.wasPressed()) {
                var user = MinecraftClient.getInstance().player;
                var result = MinecraftClient.getInstance().crosshairTarget;
                if (hasTrinkets(ServerNetworking.active_skills_with_target, user)) {
                    if (result != null && result.getType() == HitResult.Type.ENTITY) {
                        var hitResult = (EntityHitResult) result; var entity = hitResult.getEntity();
                        if (entity instanceof PlayerEntity player) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeUuid(player.getUuid());
                            ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL_TARGET, buf);
                            return;
                        }
                    }
                }
                if (hasTrinkets(ServerNetworking.active_skills, user)) ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, new PacketByteBuf(Unpooled.buffer()));
            }
        });
    }
}
