package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At(value = "HEAD"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        KeyInputCallback.KEY_INPUT.invoker().onKeyInput(key, scancode, action, modifiers);
    }
}

@Mixin(Mouse.class)
class MouseMixin {

    @Shadow private double x;
    @Shadow private double y;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;lockCursor()V"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        com.amotassic.dabaosword.client.ChangeSkillRender.handleSkillSelect(x, y);
    }
}
