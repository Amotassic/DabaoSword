package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.item.skillcard.ActiveSkill;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerNetworking {
    public static Identifier ACTIVE_SKILL = new Identifier("dabaosword:active_skill");
    public static Identifier ACTIVE_SKILL_TARGET = new Identifier("dabaosword:active_skill_target");
    public static Item[] active_skills_with_target = {SkillCards.RENDE, SkillCards.YIJI};
    public static Item[] active_skills = {SkillCards.ZHIHENG, SkillCards.QICE, SkillCards.TAOLUAN, SkillCards.LUOSHEN, SkillCards.KUROU};

    public static void registerActiveSkillPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL, ServerNetworking::receiveActiveSkillPacket);
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL_TARGET, ServerNetworking::receiveActiveSkillTargetPacket);
    }

    private static void receiveActiveSkillTargetPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid(); PlayerEntity target = server.getPlayerManager().getPlayer(uuid);
        server.execute(() -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    ItemStack stack = entry.getRight();
                    if(Arrays.stream(active_skills_with_target).toList().contains(stack.getItem())) {
                        ActiveSkill.active(player, stack, target);
                        return;
                    }
                }
            }
        });
    }

    private static void receiveActiveSkillPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    if(Arrays.stream(active_skills).toList().contains(entry.getRight().getItem())) {
                        ActiveSkill.active(player, entry.getRight());
                        return;
                    }
                }
            }
        });
    }
}
