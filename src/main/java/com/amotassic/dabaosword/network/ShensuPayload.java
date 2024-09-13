package com.amotassic.dabaosword.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ShensuPayload(float f) implements CustomPayload {
    public static final Id<ShensuPayload> ID = new Id<>(Identifier.of("dabaosword:shensu_speed"));
    public static final PacketCodec<RegistryByteBuf, ShensuPayload> CODEC =
            PacketCodec.of(((value, buf) -> {PacketByteBuf buf1 = buf.writeFloat(value.f);}), buf -> new ShensuPayload(buf.readFloat()));

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
