package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow private float yRot;
    @Shadow private float xRot;

    @Inject(method = "update", at = @At(value = "TAIL"))
    public void setProjectionMatrix(DeltaTracker deltaTracker, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            if (!player.isSpectator() && player.hasEffect(ModItems.TURNOVER)) {
                //翻转摄像机，效果似乎更加河里了
                setRotation(yRot + 180f, xRot + 180f);
            }
        }
    }
}
