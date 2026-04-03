package com.amotassic.dabaosword.mixin.client;

import com.amotassic.dabaosword.api.event.KeyInputCallback;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {

    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    public void onKey(long handle, int action, KeyEvent input, CallbackInfo ci) {
        KeyInputCallback.KEY_INPUT.invoker().onKeyInput(input.key(), input.scancode(), action, input.modifiers());
    }
}

@Mixin(MouseHandler.class)
class MouseMixin {
    @Shadow private double xpos;
    @Shadow private double ypos;

    @Inject(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;grabMouse()V"))
    private void onMouseButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        com.amotassic.dabaosword.client.ChangeSkillRender.handleSkillSelect(xpos, ypos);
    }
}
