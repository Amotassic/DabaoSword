package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final MinecraftClient client;

    @Shadow protected abstract void loadPostProcessor(Identifier id);

    @Shadow @Nullable PostEffectProcessor postProcessor;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = client.player;
        if (player != null) {
            //如果玩家有翻面效果，玩家的视野会上下翻转
            if (!player.isSpectator() && player.hasStatusEffect(ModItems.TURNOVER)) this.loadPostProcessor(new Identifier("shaders/post/flip.json"));
            else if (postProcessor != null) {postProcessor.close();}
        }
    }

    /*"shaders/post/flip.json"          上下翻转
    *"shaders/post/bumpy.json"          立体渲染
    *"shaders/post/color_convolve.json" 鲜艳
    *"shaders/post/bits.json"           红白机
    *"shaders/post/deconverge.json"     近视
    *"shaders/post/desaturate.json"     灰
    *"shaders/post/pencil.json"         素描画
    * */
}
