package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ServerNetworking;
import com.amotassic.dabaosword.ui.PlayerInvHandledScreen;
import com.amotassic.dabaosword.ui.QiceHandledScreen;
import com.amotassic.dabaosword.ui.TaoluanHandledScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static com.amotassic.dabaosword.item.ModItems.TIESUO;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HandledScreens.register(SkillCards.TAOLUAN_SCREEN_HANDLER, TaoluanHandledScreen::new);
        HandledScreens.register(SkillCards.QICE_SCREEN_HANDLER, QiceHandledScreen::new);
        HandledScreens.register(ServerNetworking.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        SkillKeyBinds.initialize();
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.hasStatusEffect(StatusEffects.POISON)) {
                invertScreen(context);
            }
        });

        //自定义谓词，用于改变铁索连环的纹理
        ModelPredicateProviderRegistry.register(TIESUO, new Identifier("nahida"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK ? 1.0F : 0.0F;
        });
    }

    private void invertScreen(WorldRenderContext context) {
        MatrixStack matrixStack = context.matrixStack();
        MinecraftClient client = MinecraftClient.getInstance();
    }
}
