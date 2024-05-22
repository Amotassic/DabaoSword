package com.amotassic.dabaosword.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ActiveSkillPayload(int value) implements CustomPayload {
    public static final Id<ActiveSkillPayload> ID = CustomPayload.id("dabaosword:active_skill");
    public static final PacketCodec<RegistryByteBuf, ActiveSkillPayload> CODEC = PacketCodecs.INTEGER.xmap(ActiveSkillPayload::new, ActiveSkillPayload::value).cast();

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}

    @Override
    public int value() {return value;}
}
