package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static com.amotassic.dabaosword.item.ModItems.TIESUO;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HandledScreens.register(ServerNetworking.SIMPLE_MENU_HANDLER, SimpleMenuScreen::new);
        HandledScreens.register(ServerNetworking.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        HandledScreens.register(ServerNetworking.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        SkillKeyBinds.initialize();

        //自定义谓词，用于改变铁索连环的纹理
        ModelPredicateProviderRegistry.register(TIESUO, Identifier.of("nahida"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) return 0.0F;
            return livingEntity.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK ? 1.0F : 0.0F;
        });
    }
}
