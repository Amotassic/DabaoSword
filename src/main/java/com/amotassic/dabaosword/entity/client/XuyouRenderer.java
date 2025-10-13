package com.amotassic.dabaosword.entity.client;

import com.amotassic.dabaosword.entity.XuyouEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

public class XuyouRenderer extends BipedEntityRenderer<XuyouEntity, BipedEntityRenderState, XuyouModel> {
    public XuyouRenderer(EntityRendererFactory.Context context) {
        this(context, ModModelLayers.XUYOU, EntityModelLayers.PLAYER_EQUIPMENT);
    }

    @Override
    public BipedEntityRenderState createRenderState() {return new BipedEntityRenderState();}

    public XuyouRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EquipmentModelData<EntityModelLayer> equipmentModelData) {
        super(ctx, new XuyouModel(ctx.getPart(layer)), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this, EquipmentModelData.mapToEntityModel(equipmentModelData, ctx.getEntityModels(), XuyouModel::new), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return Identifier.of("dabaosword" ,"textures/entity/xuyou.png");
    }
}
