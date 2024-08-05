package com.amotassic.dabaosword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record FullInvPayload(int id) {
    public static final PacketCodec<RegistryByteBuf, FullInvPayload> FULL_INV_CODEC =
            PacketCodec.of(((value, buf) -> {
                PacketByteBuf buf1 = buf.writeInt(value.id);
            }), buf -> new FullInvPayload(buf.readInt()));
}
