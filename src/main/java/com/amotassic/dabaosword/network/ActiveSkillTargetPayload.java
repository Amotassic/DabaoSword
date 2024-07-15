package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record ActiveSkillTargetPayload(UUID uuid) implements CustomPayload {
    public static final Id<ActiveSkillTargetPayload> ID = new Id<>(Identifier.of("dabaosword:active_skill_target"));
    public static final PacketCodec<RegistryByteBuf, ActiveSkillTargetPayload> CODEC =
            PacketCodec.of(((value, buf) -> buf.writeUuid(value.uuid)), buf -> new ActiveSkillTargetPayload(buf.readUuid()));

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
