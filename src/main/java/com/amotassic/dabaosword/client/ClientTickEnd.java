package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import com.amotassic.dabaosword.network.QuickSwapPayload;
import com.amotassic.dabaosword.network.ShensuPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
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
            if (user != null) {
                if (hasTrinket(SkillCards.SHENSU, user)) {
                    Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                    float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                    ClientPlayNetworking.send(new ShensuPayload(speed));
                }

                if (SELECT_CARD.wasPressed()) {
                    int i = 0;
                    if (user.isSneaking() && client.options.sprintKey.wasPressed()) i = 3;
                    else if (user.isSneaking()) i = 1;
                    else if (client.options.sprintKey.wasPressed()) i = 2;
                    ClientPlayNetworking.send(new QuickSwapPayload(i));
                    return;
                }

                if (ACTIVE_SKILL.wasPressed()) {
                    if (isEquipped(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkillWithTarget)) {
                        if (result != null && result.getType() == HitResult.Type.ENTITY) {
                            if (((EntityHitResult) result).getEntity() instanceof PlayerEntity player) {
                                ClientPlayNetworking.send(new ActiveSkillPayload(player.getId()));
                                return;
                            }
                        }
                    }
                    if (isEquipped(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkill)) ClientPlayNetworking.send(new ActiveSkillPayload(user.getId()));
                }
            }
        });
    }
}
