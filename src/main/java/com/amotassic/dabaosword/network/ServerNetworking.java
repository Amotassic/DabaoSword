package com.amotassic.dabaosword.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworking {

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(SimplePayload.ID, SimplePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SimplePayload.ID, SimplePayload::execute);
    }
}
