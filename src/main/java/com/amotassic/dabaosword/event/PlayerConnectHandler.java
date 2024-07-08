package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.util.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.amotassic.dabaosword.item.skillcard.SkillItem.changeSkill;

public class PlayerConnectHandler implements PlayerConnectCallback {
    @Override
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {

        if (!player.getCommandTags().contains("given_skill")) {
            changeSkill(player);
            player.addCommandTag("given_skill");
        }

    }
}
