package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.QuickSwapPayload;
import com.amotassic.dabaosword.network.ShensuPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import static com.amotassic.dabaosword.util.ModTools.hasTrinket;

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

            if (hasTrinket(SkillCards.SHENSU, user)) {
                Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                ClientPlayNetworking.send(new ShensuPayload(speed));
            }

            if (SELECT_CARD.wasPressed()) {
                int i = 0;
                if (user.isSneaking() && ctrl.wasPressed()) i = 3;
                else if (ctrl.wasPressed()) i = 2;
                ClientPlayNetworking.send(new QuickSwapPayload(i));
            }
        });
    }

    private static KeyBinding keyBinding(String name, int key) {
        String category = "category.dabaosword.keybindings";
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dabaosword." + name, key, category));
    }
}
