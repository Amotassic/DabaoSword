package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ClientTickEnd {
    public static final KeyBinding ACTIVE_SKILL = keyBinding("active_skill", GLFW.GLFW_KEY_J);
    public static final KeyBinding SELECT_CARD = keyBinding("select_card", GLFW.GLFW_KEY_K);

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = client.player;
            if (user == null) return;
            var ctrl = client.options.sprintKey;
            // 当打开screen后，关闭选择技能渲染
            if (ChangeSkillRender.isRendering && client.currentScreen != null) {
                ChangeSkillRender.close();
            }

            if (ModTools.hasTrinket(SkillCards.SHENSU, user)) {
                Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                SimplePayload.sendToServer(SimplePayload.SHENSU, Float.toString(speed));
            }

            if (SELECT_CARD.wasPressed()) {
                if (user.isSneaking() && ctrl.wasPressed()) SimplePayload.sendToServer(SimplePayload.CANCEL_DODGE);
                else if (ctrl.wasPressed()) SimplePayload.sendToServer(SimplePayload.CARD_PILE);
                else SimplePayload.sendToServer(SimplePayload.QUICK_SWAP);
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
