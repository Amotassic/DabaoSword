package com.amotassic.dabaosword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record OpenInvPayload(int id) {
    public static final PacketCodec<RegistryByteBuf, OpenInvPayload> OPENINV_CODEC =
            PacketCodec.of(((value, buf) -> {
                PacketByteBuf buf1 = buf.writeInt(value.id);
            }), buf -> new OpenInvPayload(buf.readInt()));
}
