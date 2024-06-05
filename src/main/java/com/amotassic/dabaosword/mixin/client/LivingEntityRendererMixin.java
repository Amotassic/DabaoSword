package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "shouldFlipUpsideDown", at = @At("TAIL"), cancellable = true)
    private static void shouldFlipUpsideDown(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        //如果生物有翻面效果，生物的模型会上下翻转，就像Dinnerbone一样
        if (entity.hasStatusEffect(ModItems.TURNOVER)) cir.setReturnValue(true);
        if (Objects.equals(entity.getCustomName(), Text.literal("翻面"))) cir.setReturnValue(true);
    }
}
