package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ActiveSkillPayload(int id) implements CustomPayload {
    public static final Id<ActiveSkillPayload> ID = new Id<>(Identifier.of("dabaosword:active_skill_target"));
    public static final PacketCodec<RegistryByteBuf, ActiveSkillPayload> CODEC =
            PacketCodec.of(((value, buf) -> buf.writeInt(value.id)), buf -> new ActiveSkillPayload(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
