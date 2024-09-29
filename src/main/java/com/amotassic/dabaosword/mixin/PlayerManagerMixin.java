package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.event.callback.PlayerConnectCallback;
import com.amotassic.dabaosword.event.callback.PlayerRespawnCallback;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        PlayerConnectCallback.EVENT.invoker().onPlayerConnect(connection, player);
    }

    @SuppressWarnings({"checkstyle:NoWhitespaceBefore", "checkstyle:MethodName"})
    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE",
            // This target lets us modify respawn position
            target = "Lnet/minecraft/server/world/ServerWorld;getLevelProperties()Lnet/minecraft/world/WorldProperties;"
    ))
    public void onRespawnPlayer_afterSetPosition(
            ServerPlayerEntity oldPlayer, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir,
            @Local(ordinal = 1) ServerPlayerEntity serverPlayerEntity
    ) {
        PlayerRespawnCallback.EVENT.invoker().onPlayerRespawn(oldPlayer, serverPlayerEntity);
    }
}
