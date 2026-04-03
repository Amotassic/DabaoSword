package com.amotassic.dabaosword.api.event;

import com.amotassic.dabaosword.pvpgame.Game;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

/**
 * 大宝刀mod添加的全局游戏事件的tick回调。
 */
public interface PVPGameTickCallback {
    Event<PVPGameTickCallback> EVENT = EventFactory.createArrayBacked(PVPGameTickCallback.class,
        (listeners) -> (game, world) -> {
            for (PVPGameTickCallback event : listeners) {
                event.onGameTick(game, world);
            }
        });

    void onGameTick(Game game, ServerLevel world);
}
