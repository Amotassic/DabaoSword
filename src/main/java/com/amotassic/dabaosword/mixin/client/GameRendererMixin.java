package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow private float yaw;

    @Shadow private float pitch;

    @Inject(method = "update", at = @At(value = "TAIL"))
    public void setProjectionMatrix(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (!player.isSpectator() && player.hasStatusEffect(ModItems.TURNOVER)) {
                //翻转摄像机，效果似乎更加河里了
                setRotation(yaw + 180f, pitch + 180f);
            }
        }
    }
}
