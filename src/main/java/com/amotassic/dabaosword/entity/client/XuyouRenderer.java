package com.amotassic.dabaosword.entity.client;

import com.amotassic.dabaosword.entity.XuyouEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

public class XuyouRenderer extends BipedEntityRenderer<XuyouEntity, BipedEntityRenderState, XuyouModel> {
    public XuyouRenderer(EntityRendererFactory.Context context) {
        this(context, ModModelLayers.XUYOU, EntityModelLayers.PLAYER_INNER_ARMOR, EntityModelLayers.PLAYER_OUTER_ARMOR);
    }

    @Override
    public BipedEntityRenderState createRenderState() {return new BipedEntityRenderState();}

    public XuyouRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer innerModel, EntityModelLayer outerModel) {
        super(ctx, new XuyouModel(ctx.getPart(layer)), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this, new XuyouModel(ctx.getPart(innerModel)), new XuyouModel(ctx.getPart(outerModel)), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return Identifier.of("dabaosword" ,"textures/entity/xuyou.png");
    }
}
