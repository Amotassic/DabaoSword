package com.amotassic.dabaosword.entity.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class XuyouModel extends HumanoidModel<HumanoidRenderState> {
    public XuyouModel(ModelPart root) {super(root);}

    public static LayerDefinition getTexturedModelData() {
        return LayerDefinition.create(createMesh(CubeDeformation.NONE, 0), 64, 64);
    }
}
