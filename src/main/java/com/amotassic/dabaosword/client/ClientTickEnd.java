package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class ClientTickEnd {
    public static final KeyMapping ACTIVE_SKILL = keyBinding("active_skill", GLFW.GLFW_KEY_J);
    public static final KeyMapping SELECT_CARD = keyBinding("select_card", GLFW.GLFW_KEY_K);

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = client.player;
            if (user == null) return;
            var ctrl = client.options.keySprint;
            // 当打开screen后，关闭选择技能渲染
            if (ChangeSkillRender.isRendering && client.screen != null) {
                ChangeSkillRender.close();
            }

            if (ModTools.hasTrinket(SkillCards.SHENSU, user)) {
                Vec3 lastPos = new Vec3(user.xOld, user.yOld, user.zOld);
                float speed = (float) (user.position().distanceTo(lastPos) * 20);
                SimplePayload.sendToServer(SimplePayload.SHENSU, Float.toString(speed));
            }

            if (SELECT_CARD.consumeClick()) {
                if (user.isShiftKeyDown() && ctrl.consumeClick()) SimplePayload.sendToServer(SimplePayload.CANCEL_DODGE);
                else if (ctrl.consumeClick()) SimplePayload.sendToServer(SimplePayload.CARD_PILE);
                else SimplePayload.sendToServer(SimplePayload.QUICK_SWAP);
            }
        });
    }

    private static final KeyMapping.Category category = KeyMapping.Category.register(DabaoSword.id("key"));
    private static KeyMapping keyBinding(String name, int key) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping("key.dabaosword." + name, key, category));
    }
}
