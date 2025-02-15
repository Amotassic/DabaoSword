package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At(value = "TAIL"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyInputCallback.KEY_INPUT.invoker().onKeyInput(key, scancode, action, modifiers);
    }
}
