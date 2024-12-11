package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ServerNetworking;
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
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import static com.amotassic.dabaosword.util.ModTools.hasTrinket;
import static com.amotassic.dabaosword.util.ModTools.isEquipped;

public class ClientTickEnd {
    private static final KeyBinding ACTIVE_SKILL = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.active_skill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.dabaosword.keybindings"));

    private static final KeyBinding SELECT_CARD = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.select_card", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.dabaosword.keybindings"));

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = MinecraftClient.getInstance().player;
            var result = MinecraftClient.getInstance().crosshairTarget;
            PacketByteBuf buf = PacketByteBufs.create();
            if (user != null) {
                if (hasTrinket(SkillCards.SHENSU, user)) {
                    Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                    float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                    PacketByteBuf buf1 = PacketByteBufs.create();
                    buf1.writeFloat(speed);
                    ClientPlayNetworking.send(ServerNetworking.SHENSU, buf1);
                }

                if (SELECT_CARD.wasPressed()) {
                    if (user.isSneaking() && client.options.sprintKey.wasPressed()) buf.writeInt(3);
                    else if (user.isSneaking()) buf.writeInt(1);
                    else if (client.options.sprintKey.wasPressed()) buf.writeInt(2);
                    else buf.writeInt(0);
                    ClientPlayNetworking.send(ServerNetworking.SELECT_CARD, buf);
                    return;
                }

                if (result != null && result.getType() == HitResult.Type.ENTITY) {
                    if (((EntityHitResult) result).getEntity() instanceof PlayerEntity player) {
                        if (ACTIVE_SKILL.wasPressed() && isEquipped(user, s -> s.getItem() instanceof SkillItem.ActiveSkillWithTarget)) {
                            buf.writeUuid(player.getUuid());
                            ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, buf);
                            return;
                        }
                    }
                }
                if (ACTIVE_SKILL.wasPressed() && isEquipped(user, s -> s.getItem() instanceof SkillItem.ActiveSkill)) {
                    buf.writeUuid(user.getUuid());
                    ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, buf);
                }
            }
        });
    }
}
