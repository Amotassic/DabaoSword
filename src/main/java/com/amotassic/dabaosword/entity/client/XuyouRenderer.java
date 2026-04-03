package com.amotassic.dabaosword.entity.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.entity.XuyouEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class XuyouRenderer extends HumanoidMobRenderer<XuyouEntity, HumanoidRenderState, XuyouModel> {
    public XuyouRenderer(EntityRendererProvider.Context context) {
        this(context, ModModelLayers.XUYOU, ModelLayers.PLAYER_ARMOR);
    }

    @Override
    public HumanoidRenderState createRenderState() {return new HumanoidRenderState();}

    public XuyouRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation layer, ArmorModelSet<ModelLayerLocation> equipmentModelData) {
        super(ctx, new XuyouModel(ctx.bakeLayer(layer)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, ArmorModelSet.bake(equipmentModelData, ctx.getModelSet(), XuyouModel::new), ctx.getEquipmentRenderer()));
    }

    @Override
    public @NonNull Identifier getTextureLocation(HumanoidRenderState state) {
        return DabaoSword.id("textures/entity/xuyou.png");
    }
}
