package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ClientTickEnd {
    public static final KeyBinding ACTIVE_SKILL = keyBinding("active_skill", GLFW.GLFW_KEY_J);
    public static final KeyBinding SELECT_CARD = keyBinding("select_card", GLFW.GLFW_KEY_K);

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = client.player;
            if (user == null) return;
            var ctrl = client.options.sprintKey;
            PacketByteBuf buf = PacketByteBufs.create();
            // 当打开screen后，关闭选择技能渲染
            if (ChangeSkillRender.isRendering && client.currentScreen != null) {
                ChangeSkillRender.close();
            }

            if (hasTrinket(SkillCards.SHENSU, user)) {
                Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                PacketByteBuf buf1 = PacketByteBufs.create();
                buf1.writeFloat(speed);
                ClientPlayNetworking.send(ServerNetworking.SHENSU, buf1);
            }

            if (SELECT_CARD.wasPressed()) {
                if (user.isSneaking() && ctrl.wasPressed()) buf.writeInt(3);
                else if (ctrl.wasPressed()) buf.writeInt(2);
                else buf.writeInt(0);
                ClientPlayNetworking.send(ServerNetworking.SELECT_CARD, buf);
            }
        });
    }

/*    public static boolean clientFly = false;

    private static void doClientFly(MinecraftClient mc, ClientPlayerEntity player) {
        var options = mc.options;

        boolean up = options.jumpKey.isPressed();
        boolean down = options.sneakKey.isPressed();
        if (up || down) {
            if (up) {
                player.jump();
                player.fallDistance = 0.0F;
            }
            int i = 0;
            if (down) i--;

            if (i != 0) {
                Vec3d v = player.getVelocity();
                Vec3d v2 = new Vec3d(v.x, (float)i * player.getAbilities().getFlySpeed() * 10F, v.z);
                player.setVelocity(v2);
            }
        } else {
            Vec3d v = player.getVelocity(); Vec3d v2 = new Vec3d(v.x, 0, v.z);
            player.setVelocity(v2);
        }
    }*/

    private static KeyBinding keyBinding(String name, int key) {
        String category = "category.dabaosword.keybindings";
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dabaosword." + name, key, category));
    }
}
