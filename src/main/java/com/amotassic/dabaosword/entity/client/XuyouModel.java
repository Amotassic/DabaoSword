package com.amotassic.dabaosword.entity.client;

import com.amotassic.dabaosword.entity.XuyouEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class XuyouModel<T extends XuyouEntity> extends BipedEntityModel<T> {
    public XuyouModel(ModelPart root) {super(root);}

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(getModelData(Dilation.NONE, 0), 64, 64);
    }
}
