package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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

import static com.amotassic.dabaosword.util.ModTools.c;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow @Final private MatrixStack matrices;

    @Shadow abstract void drawTexture(Identifier texture, int x1, int x2, int y1, int y2, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight);

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        var card = c(stack);
        var s = card.suit; var r = card.rank;
        if (s != Suit.None && r != Rank.None) {
            Identifier suit = Identifier.of("dabaosword", "textures/item/suit/" + getSuitName(s) + ".png");
            Identifier rank = Identifier.of("dabaosword", "textures/item/rank2/" + getRankName(s, r) + ".png");
            if (stack.getCount() == 1) this.matrices.translate(0.0f, 0.0f, 200.0f);
            drawTexture(suit, x, x + 5, y, y + 5, 0, 64, 64, 0, 0, 64, 64);
            drawTexture(rank, x + 5, x + 11, y, y + 5, 0, 64, 64, 0, 0, 64, 64);
        }
    }

    @ModifyArgs(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"))
    private void drawItem(Args args) {
        ItemStack stack = args.get(0);
        if (ModTools.isCard(stack) || stack.isOf(ModItems.GAIN_CARD)) {
            String path = "card/" + stack.getItem().toString().split(":")[1];
            if (stack.isOf(ModItems.TIESUO) && client.player != null && client.player.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) path = "nahida";
            ModelIdentifier modelId = ModelIdentifier.ofInventoryVariant(Identifier.of("dabaosword", path));
            BakedModel model = client.getItemRenderer().getModels().getModelManager().getModel(modelId);
            args.set(7, model);
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
