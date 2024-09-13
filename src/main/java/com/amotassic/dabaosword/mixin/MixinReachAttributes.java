package com.amotassic.dabaosword.mixin;

import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityAttributes.class)
public class MixinReachAttributes {

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 64.0))
    private static double modify(double constant) {
        if (constant == 64.0) return 256.0;
        return constant;
    }
}
