package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "isEntityUpsideDown", at = @At("TAIL"), cancellable = true)
    private void shouldFlipUpsideDown(T entity, CallbackInfoReturnable<Boolean> cir) {
        //如果生物有翻面效果，生物的模型会上下翻转，就像Dinnerbone一样
        if (entity.hasEffect(ModItems.TURNOVER)) cir.setReturnValue(true);
        if (Objects.equals(entity.getCustomName(), Component.literal("翻面"))) cir.setReturnValue(true);
    }
}
