package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record TaoluanPayload(int label) {
    public static final PacketCodec<RegistryByteBuf, TaoluanPayload> TAOLUANCODEC = PacketCodecs.INTEGER.xmap(TaoluanPayload::new, TaoluanPayload::label).cast();
}
