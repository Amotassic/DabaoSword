package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.amotassic.dabaosword.util.ModTools.c;

@Mixin(GuiGraphicsExtractor.class)
public abstract class DrawContextMixin {

    @Shadow
    protected abstract void innerBlit(RenderPipeline renderPipeline, Identifier location, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color);

    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemBar(Lnet/minecraft/world/item/ItemStack;II)V"))
    public void drawItemInSlot(Font font, ItemStack stack, int x, int y, String countText, CallbackInfo ci) {
        var card = c(stack);
        var s = card.suit; var r = card.rank;
        if (s != Suit.None) {
            Identifier suit = DabaoSword.id("textures/item/suit/" + getSuitName(s) + ".png");
            innerBlit(RenderPipelines.GUI_TEXTURED, suit, x, x + 5, y, y + 5, 0, 1, 0, 1, -1);
        }
        if (r != Rank.None) {
            Identifier rank = DabaoSword.id("textures/item/rank2/" + getRankName(s, r) + ".png");
            innerBlit(RenderPipelines.GUI_TEXTURED, rank, x + 5, x + 11, y, y + 5, 0, 1, 0, 1, -1);
        }
    }

    @Unique private static String getSuitName(Suit s) {
        return switch (s) {
            case Heart -> "heart";
            case Diamond -> "diamond";
            case Spade -> "spade_w";
            case Club -> "club_w";
            case None -> "";
        };
    }

    @Unique private static String getRankName(Suit s, Rank r) {
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
