package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow public abstract int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

    @Shadow @Final private MatrixStack matrices;

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        var sr = ModTools.getSuitAndRank(stack);
        if (sr != null) {
            Text text = Text.translatable("card.suit_and_rank", sr.getLeft().suit, sr.getRight().rank);
            int color = ModTools.isRedCard.test(stack) ? 0xFF5555 : 0xFFFFFF;
            this.matrices.translate(0.0f, 0.0f, 200.0f);
            drawText(textRenderer, text, x, y, color, false);
        }
    }
}
