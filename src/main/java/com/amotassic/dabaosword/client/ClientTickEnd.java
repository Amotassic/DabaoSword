package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ServerNetworking;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.hasTrinket;

public class ClientTickEnd {
    private static final KeyBinding ACTIVE_SKILL = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.dabaosword.active_skill", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.dabaosword.keybindings"));

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var user = MinecraftClient.getInstance().player;
            var result = MinecraftClient.getInstance().crosshairTarget;
            if (user != null) {
                if (result != null && result.getType() == HitResult.Type.ENTITY) {
                    var hitResult = (EntityHitResult) result; var entity = hitResult.getEntity();
                    if (entity instanceof PlayerEntity player) {
                        if (ACTIVE_SKILL.wasPressed() && haveSkill(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkillWithTarget)) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeUuid(player.getUuid());
                            ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, buf);
                        }
                        return;
                    }
                }
                if (ACTIVE_SKILL.wasPressed() && haveSkill(user, stack -> stack.getItem() instanceof SkillItem.ActiveSkill)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeUuid(user.getUuid());
                    ClientPlayNetworking.send(ServerNetworking.ACTIVE_SKILL, buf);
                }
                if (hasTrinket(SkillCards.SHENSU, user)) {
                    Vec3d lastPos = new Vec3d(user.lastRenderX, user.lastRenderY, user.lastRenderZ);
                    float speed = (float) (user.getPos().distanceTo(lastPos) * 20);
                    if (speed > 0) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeFloat(speed);
                        ClientPlayNetworking.send(ServerNetworking.SHENSU, buf);
                    }
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
