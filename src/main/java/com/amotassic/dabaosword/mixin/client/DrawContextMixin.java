package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.amotassic.dabaosword.util.ModTools.c;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow protected abstract void drawTexturedQuad(RenderPipeline pipeline, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);

    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemBar(Lnet/minecraft/item/ItemStack;II)V"))
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        var card = c(stack);
        var s = card.suit; var r = card.rank;
        if (s != Suit.None && r != Rank.None) {
            Identifier suit = Identifier.of("dabaosword", "textures/item/suit/" + getSuitName(s) + ".png");
            Identifier rank = Identifier.of("dabaosword", "textures/item/rank2/" + getRankName(s, r) + ".png");
            drawTexturedQuad(RenderPipelines.GUI_TEXTURED, suit, x, x + 5, y, y + 5, 0, 1, 0, 1, -1);
            drawTexturedQuad(RenderPipelines.GUI_TEXTURED, rank, x + 5, x + 11, y, y + 5, 0, 1, 0, 1, -1);
        }
    }

    @Unique private String getSuitName(Suit s) {
        return switch (s) {
            case Heart -> "heart";
            case Diamond -> "diamond";
            case Spade -> "spade_w";
            case Club -> "club_w";
            case None -> "";
        };
    }

    @Unique private String getRankName(Suit s, Rank r) {
        return switch (s) {
            case Heart, Diamond -> switch (r) {
                case Ace -> "ar";
                case Two -> "2r";
                case Three -> "3r";
                case Four -> "4r";
                case Five -> "5r";
                case Six -> "6r";
                case Seven -> "7r";
                case Eight -> "8r";
                case Nine -> "9r";
                case Ten -> "10r";
                case Jack -> "jr";
                case Queen -> "qr";
                case King -> "kr";
                case None -> "";
            };
            case Spade, Club -> switch (r) {
                case Ace -> "ab";
                case Two -> "2b";
                case Three -> "3b";
                case Four -> "4b";
                case Five -> "5b";
                case Six -> "6b";
                case Seven -> "7b";
                case Eight -> "8b";
                case Nine -> "9b";
                case Ten -> "10b";
                case Jack -> "jb";
                case Queen -> "qb";
                case King -> "kb";
                case None -> "";
            };
            case None -> "";
        };
    }
}
