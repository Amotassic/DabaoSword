package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.util.PlayerRespawnCallback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @SuppressWarnings({"checkstyle:NoWhitespaceBefore", "checkstyle:MethodName"})
    @Inject(method = "respawnPlayer", at = @At(
            value = "INVOKE",
            // This target lets us modify respawn position
            target = "Lnet/minecraft/world/World;getLevelProperties()Lnet/minecraft/world/WorldProperties;"
    ), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRespawnPlayer_afterSetPosition(
            ServerPlayerEntity oldServerPlayerEntity, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir
            , BlockPos blockPos
            , float f
            , boolean bl
            , ServerWorld serverWorld
            , Optional optional
            , ServerWorld serverWorld2
            , ServerPlayerEntity serverPlayerEntity
    ) {
        PlayerRespawnCallback.EVENT.invoker().onPlayerRespawn(oldServerPlayerEntity, serverPlayerEntity);
    }
}
