package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class TiesuoRenderer<T extends PlayerEntity, M extends EntityModel<T> & ModelWithHead & ModelWithArms>
        extends HeldItemFeatureRenderer<T, M> {
    private final HeldItemRenderer playerHeldItemRenderer;

    public TiesuoRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.playerHeldItemRenderer = heldItemRenderer;
    }

    @Override
    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isOf(ModItems.TIESUO) && entity.getActiveItem() == stack && entity.handSwingTicks == 0) {
            this.renderTiesuo(entity, stack, arm, matrices, vertexConsumers, light);
        } else {
            super.renderItem(entity, stack, transformationMode, arm, matrices, vertexConsumers, light);
        }
    }

    private void renderTiesuo(LivingEntity entity, ItemStack stack, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        ModelPart modelPart = this.getContextModel().getHead();
        float f = modelPart.pitch;
        modelPart.pitch = MathHelper.clamp(modelPart.pitch, -0.5235988f, 1.5707964f);
        modelPart.rotate(matrices);
        modelPart.pitch = f;
        HeadFeatureRenderer.translate(matrices, false);
        boolean bl = arm == Arm.LEFT;
        matrices.translate((bl ? -2.5f : 2.5f) / 16.0f, -0.0625f, 0.0f);
        this.playerHeldItemRenderer.renderItem(entity, stack, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
