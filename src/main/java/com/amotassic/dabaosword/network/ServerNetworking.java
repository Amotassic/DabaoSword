package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.event.callback.ActiveSkillCallback;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.trinketItem;

public class ServerNetworking {
    public static Identifier ACTIVE_SKILL = new Identifier("dabaosword:active_skill");
    public static Identifier SHENSU = new Identifier("dabaosword:shensu_speed");

    public static void registerActiveSkillPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL, ServerNetworking::receiveActiveSkillPacket);
        ServerPlayNetworking.registerGlobalReceiver(SHENSU, ServerNetworking::receiveShensuPacket);
    }

    private static void receiveActiveSkillPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid(); PlayerEntity target = server.getPlayerManager().getPlayer(uuid);
        server.execute(() -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    ItemStack stack = entry.getRight();
                    if(stack.getItem() instanceof SkillItem.ActiveSkillWithTarget && target != player) {
                        ActiveSkillCallback.EVENT.invoker().activeSkill(player, stack, target);
                        return;
                    }
                    if(stack.getItem() instanceof SkillItem.ActiveSkill && target == player) {
                        ActiveSkillCallback.EVENT.invoker().activeSkill(player, stack, player);
                        return;
                    }
                }
            }
        });
    }

    private static void receiveShensuPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        float speed = buf.readFloat();
        ItemStack stack = trinketItem(SkillCards.SHENSU, player);
        if (stack != null) {
            NbtCompound nbt = stack.getOrCreateNbt(); nbt.putFloat("speed", speed); stack.setNbt(nbt);
            //if (stack.getNbt() != null && stack.getNbt().getFloat("speed") > 0) player.sendMessage(Text.literal("Speed: " + speed), true);
        }
    }
}
