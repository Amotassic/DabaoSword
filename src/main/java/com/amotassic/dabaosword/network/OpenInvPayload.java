package com.amotassic.dabaosword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.UUID;

public record OpenInvPayload(UUID uuid) {
    public static final PacketCodec<RegistryByteBuf, OpenInvPayload> OPENINV_CODEC =
            PacketCodec.of(((value, buf) -> {
                PacketByteBuf buf1 = buf.writeUuid(value.uuid);
            }), buf -> new OpenInvPayload(buf.readUuid()));
}
