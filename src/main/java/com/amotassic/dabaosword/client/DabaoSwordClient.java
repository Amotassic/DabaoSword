package com.amotassic.dabaosword.client;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.FullInvHandledScreen;
import com.amotassic.dabaosword.ui.PileHandledScreen;
import com.amotassic.dabaosword.ui.PlayerInvHandledScreen;
import com.amotassic.dabaosword.ui.SimpleMenuScreen;
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static net.minecraft.client.item.ModelPredicateProviderRegistry.register;

public class DabaoSwordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TiesuoHud());
        HandledScreens.register(ModItems.SIMPLE_MENU_HANDLER, SimpleMenuScreen::new);
        HandledScreens.register(ModItems.PLAYER_INV_SCREEN_HANDLER, PlayerInvHandledScreen::new);
        HandledScreens.register(ModItems.FULL_INV_SCREEN_HANDLER, FullInvHandledScreen::new);
        HandledScreens.register(ModItems.PILE_SCREEN_HANDLER, PileHandledScreen::new);
        ClientTickEnd.initialize();
        registerPredicates();
    }

    private void registerPredicates() {
        //用于改变铁索连环的纹理
        register(ModItems.TIESUO, Identifier.of("nahida"), (stack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) return 0.0F;
            return livingEntity.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK) ? 1.0F : 0.0F;
        });
        //用于添加卡牌的花色和点数
        var itemList = Registries.ITEM.stream().filter(item -> item instanceof Card).toList();
        for (var item : itemList) {registerCustomModelPredicate(item);}
    }

    private void registerCustomModelPredicate(Item item) {
        register(item, Identifier.of(item.toString() + "_sr"), (stack, clientWorld, livingEntity, seed) -> {
            var sr = ModTools.getSuitAndRank(stack);
            if (sr == null) return 0.0F;
            int suit = sr.getLeft().ordinal();
            int rank = sr.getRight().ordinal() + 1;
            return (float) (0.13 * suit + 0.01 * rank);
        });
    }
}
