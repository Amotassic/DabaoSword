package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import com.amotassic.dabaosword.network.QuickSwapPayload;
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
import org.lwjgl.glfw.GLFW;

import java.util.Optional;
import java.util.function.Predicate;

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
                if (SELECT_CARD.wasPressed()) {
                    int i = 0;
                    if (user.isSneaking() && client.options.sprintKey.wasPressed()) i = 3;
                    else if (user.isSneaking()) i = 1;
                    else if (client.options.sprintKey.wasPressed()) i = 2;
                    ClientPlayNetworking.send(new QuickSwapPayload(i));
                    return;
                }

                if (ACTIVE_SKILL.wasPressed()) {
                    if (haveSkill(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkillWithTarget)) {
                        if (result != null && result.getType() == HitResult.Type.ENTITY) {
                            if (((EntityHitResult) result).getEntity() instanceof PlayerEntity player) {
                                ClientPlayNetworking.send(new ActiveSkillPayload(player.getId()));
                                return;
                            }
                        }
                    }
                    if (haveSkill(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkill)) ClientPlayNetworking.send(new ActiveSkillPayload(user.getId()));
                }
            }
        });
    }

    public static boolean haveSkill(PlayerEntity player, Predicate<ItemStack> predicate) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return false;
        TrinketComponent component = optionalComponent.get();
        ItemStack itemStack = component.getEquipped(predicate).stream().map(Pair::getRight).findFirst().orElse(null);
        return itemStack != null;
    }
}
