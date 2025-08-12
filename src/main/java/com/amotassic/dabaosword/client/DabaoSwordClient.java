package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.event.KeyInputCallback;
import com.amotassic.dabaosword.entity.ModEntity;
import com.amotassic.dabaosword.entity.client.ModModelLayers;
import com.amotassic.dabaosword.entity.client.XuyouModel;
import com.amotassic.dabaosword.entity.client.XuyouRenderer;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.ui.FullInvHandledScreen;
import com.amotassic.dabaosword.ui.PileHandledScreen;
import com.amotassic.dabaosword.ui.PlayerInvHandledScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static com.amotassic.dabaosword.util.ModTools.c;
import static net.minecraft.client.item.ModelPredicateProviderRegistry.register;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HudRenderCallback.EVENT.register(new ChangeSkillRender());
        HandledScreens.register(ModItems.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        HandledScreens.register(ModItems.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        HandledScreens.register(ModItems.PILE_SCREEN_HANDLER, PileHandledScreen::new);
        ClientTickEnd.initialize();
        registerPredicates();
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.XUYOU, XuyouModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntity.XUYOU, XuyouRenderer::new);
        KeyInputCallback.KEY_INPUT.register(new KeyInputHandler());
    }

    private void registerPredicates() {
        //用于改变铁索连环的纹理
        register(ModItems.TIESUO, new Identifier("nahida"), (stack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) return 0.0F;
            return livingEntity.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK) ? 1.0F : 0.0F;
        });
        //用于添加卡牌的花色和点数
        var itemList = Registries.ITEM.stream().filter(item -> item instanceof CardItem).toList();
        for (var item : itemList) {registerCustomModelPredicate(item);}
    }

    private void registerCustomModelPredicate(Item item) {
        register(item, Identifier.of(DabaoSword.MOD_ID, item.toString() + "_sr"), (stack, clientWorld, livingEntity, seed) -> {
            var card = c(stack);
            var s = card.suit; var r = card.rank;
            if (s != Suit.None && r != Rank.None) {
                int suit = s.ordinal();
                int rank = r.ordinal() + 1;
                return (float) (0.13 * suit + 0.01 * rank);
            }
            return 0.0F;
        });
    }
}
