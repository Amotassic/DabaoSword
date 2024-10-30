package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record QuickSwapPayload(int id) implements CustomPayload {
    public static final Id<QuickSwapPayload> ID = new Id<>(Identifier.of("dabaosword:quick_swap"));
    public static final PacketCodec<RegistryByteBuf, QuickSwapPayload> CODEC =
            PacketCodec.of(((value, buf) -> buf.writeInt(value.id)), buf -> new QuickSwapPayload(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
