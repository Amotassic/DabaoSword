package com.amotassic.dabaosword.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyInputCallback {
    Event<KeyInputCallback> KEY_INPUT = EventFactory.createArrayBacked(KeyInputCallback.class,
            (listeners) -> (key, scancode, action, modifiers) -> {
                for (KeyInputCallback event : listeners) {
                    event.onKeyInput(key, scancode, action, modifiers);
                }
            });

    void onKeyInput(int key, int scancode, int action, int modifiers);
}
