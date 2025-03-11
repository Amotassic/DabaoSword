package com.amotassic.dabaosword.entity.client;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class XuyouModel extends BipedEntityModel<BipedEntityRenderState> {
    public XuyouModel(ModelPart root) {super(root);}

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(getModelData(Dilation.NONE, 0), 64, 64);
    }
}
