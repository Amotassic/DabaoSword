package com.amotassic.dabaosword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenScreenPayload(int id, boolean bl, String str) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, OpenScreenPayload> CODEC = PacketCodec.of((value, buf) -> {
            buf.writeInt(value.id);
            buf.writeBoolean(value.bl);
            buf.writeString(value.str);
        }, buf -> new OpenScreenPayload(buf.readInt(), buf.readBoolean(), buf.readString()));

    public Id<? extends CustomPayload> getId() {return new Id<>(Identifier.of("dabaosword:open_screen"));}
}
