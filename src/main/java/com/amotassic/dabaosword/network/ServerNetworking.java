package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.item.skillcard.ActiveSkill;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;

public class ServerNetworking {

    public static void registerActiveSkill() {
        PayloadTypeRegistry.playC2S().register(ActiveSkillPayload.ID, ActiveSkillPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ActiveSkillPayload.ID, (payload, context) -> {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(context.player());

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    if(entry.getRight().getItem() instanceof ActiveSkill) {
                        ActiveSkill.active(context.player(), entry.getRight());
                        return;
                    }
                }
            }
        });
    }
}
