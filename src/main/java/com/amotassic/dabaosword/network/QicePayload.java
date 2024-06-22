package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record QicePayload(int label) {
    public static final PacketCodec<RegistryByteBuf, QicePayload> QICECODEC = PacketCodec.tuple(PacketCodecs.INTEGER, QicePayload::label, QicePayload::new);
}
