package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ClientTickEnd {
    public static final KeyBinding ACTIVE_SKILL = keyBinding("active_skill", GLFW.GLFW_KEY_J);
    public static final KeyBinding SELECT_CARD = keyBinding("select_card", GLFW.GLFW_KEY_K);

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = client.player;
            var ctrl = client.options.sprintKey;
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
                    if (user.isSneaking() && ctrl.wasPressed()) buf.writeInt(3);
                    else if (user.isSneaking()) buf.writeInt(1);
                    else if (ctrl.wasPressed()) buf.writeInt(2);
                    else buf.writeInt(0);
                    ClientPlayNetworking.send(ServerNetworking.SELECT_CARD, buf);
                    return;
                }

                var result = client.crosshairTarget; LivingEntity target;
                if (result instanceof EntityHitResult eResult && eResult.getEntity() instanceof LivingEntity entity) {
                    target = entity;
                } else target = user;

                if (ACTIVE_SKILL.wasPressed() && isEquipped(user, s -> s(s).isActiveSkill())) {
                    buf.writeInt(target.getId());
                    ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, buf);
                }
            }
        });
    }

    private static KeyBinding keyBinding(String name, int key) {
        String category = "category.dabaosword.keybindings";
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dabaosword." + name, key, category));
    }
}
