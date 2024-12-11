package com.amotassic.dabaosword.entity.client;

import com.amotassic.dabaosword.entity.XuyouEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class XuyouRenderer extends BipedEntityRenderer<XuyouEntity, XuyouModel<XuyouEntity>> {
    public XuyouRenderer(EntityRendererFactory.Context context) {
        this(context, ModModelLayers.XUYOU, EntityModelLayers.PLAYER_INNER_ARMOR, EntityModelLayers.PLAYER_OUTER_ARMOR);
    }

    public XuyouRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(ctx, new XuyouModel<>(ctx.getPart(layer)), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this, new XuyouModel<>(ctx.getPart(legArmorLayer)), new XuyouModel<>(ctx.getPart(bodyArmorLayer)), ctx.getModelManager()));
    }

    @Override
    public Identifier getTexture(XuyouEntity entity) {
        return new Identifier("dabaosword" ,"textures/entity/xuyou.png");
    }
}
