package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import com.amotassic.dabaosword.network.ShensuPayload;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static com.amotassic.dabaosword.util.ModTools.hasTrinket;

public class ClientTickEnd {
    private static final KeyBinding ACTIVE_SKILL = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.active_skill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.dabaosword.keybindings"));

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = MinecraftClient.getInstance().player;
            var result = MinecraftClient.getInstance().crosshairTarget;
            if (user != null) {
                if (ACTIVE_SKILL.wasPressed()) {
                    if (hasActiveSkillWithTarget(user)) {
                        if (result != null && result.getType() == HitResult.Type.ENTITY) {
                            var hitResult = (EntityHitResult) result; var entity = hitResult.getEntity();
                            if (entity instanceof PlayerEntity player) {
                                ClientPlayNetworking.send(new ActiveSkillPayload(player.getUuid()));
                                return;
                            }
                        }
                    }
                    if (hasActiveSkill(user)) ClientPlayNetworking.send(new ActiveSkillPayload(user.getUuid()));
                }
                if (hasTrinket(SkillCards.SHENSU, user)) {
                    Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                    float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                    if (speed > 0) ClientPlayNetworking.send(new ShensuPayload(speed));
                }
            }
        });
    }

    public static boolean hasActiveSkill(PlayerEntity player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return false;
        TrinketComponent component = optionalComponent.get();
        ItemStack itemStack = component.getEquipped(stack -> stack.getItem() instanceof SkillItem.ActiveSkill).stream().map(Pair::getRight).findFirst().orElse(null);
        return itemStack != null;
    }

    public static boolean hasActiveSkillWithTarget(PlayerEntity player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return false;
        TrinketComponent component = optionalComponent.get();
        ItemStack itemStack = component.getEquipped(stack -> stack.getItem() instanceof SkillItem.ActiveSkillWithTarget).stream().map(Pair::getRight).findFirst().orElse(null);
        return itemStack != null;
    }
}
