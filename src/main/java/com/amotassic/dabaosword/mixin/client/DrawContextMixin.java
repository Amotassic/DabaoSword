package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow @Final private MatrixStack matrices;

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void drawTexturedQuad(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);

    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemBar(Lnet/minecraft/item/ItemStack;II)V"))
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        var sr = ModTools.getSuitAndRank(stack);
        if (sr != null) {
            Card.Suits s = sr.getLeft(); Card.Ranks r = sr.getRight();
            Identifier suit = Identifier.of("dabaosword", "textures/item/suit/" + getSuitName(s) + ".png");
            Identifier rank = Identifier.of("dabaosword", "textures/item/rank2/" + getRankName(s, r) + ".png");
            this.matrices.translate(0.0f, 0.0f, 200.0f);
            drawTexturedQuad(RenderLayer::getGuiTextured, suit, x, x + 5, y, y + 5, 0, 1, 0, 1, -1);
            drawTexturedQuad(RenderLayer::getGuiTextured, rank, x + 5, x + 11, y, y + 5, 0, 1, 0, 1, -1);
            this.matrices.translate(0.0f, 0.0f, -200.0f);
        }
    }

    @ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"))
    private void drawItem(Args args) {
        ItemStack stack = args.get(0);
        if (ModTools.isCard(stack) || stack.isOf(ModItems.GAIN_CARD)) {
            String path = "card/" + stack.getItem().toString().split(":")[1];
            if (stack.isOf(ModItems.TIESUO) && client.player != null && client.player.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) path = "nahida";
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword", path));
            BakedModel model = ((ItemModelAccessor) client.getItemRenderer()).getModels().getModel(modelId.id());
            args.set(7, model);
        }
    }

    @Unique private String getSuitName(Card.Suits s) {
        return switch (s) {
            case Heart -> "heart";
            case Diamond -> "diamond";
            case Spade -> "spade_w";
            case Club -> "club_w";
        };
    }

    @Unique private String getRankName(Card.Suits s, Card.Ranks r) {
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
            };
        };
    }
}
