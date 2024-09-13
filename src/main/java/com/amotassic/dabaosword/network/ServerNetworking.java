package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.event.callback.ActiveSkillCallback;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.trinketItem;

public class ServerNetworking {

    public static void registerActiveSkill() {
        PayloadTypeRegistry.playC2S().register(ActiveSkillPayload.ID, ActiveSkillPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShensuPayload.ID, ShensuPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ActiveSkillPayload.ID, (payload, context) -> {
            PlayerEntity player = context.player();
            UUID uuid = payload.uuid(); PlayerEntity target = context.player().server.getPlayerManager().getPlayer(uuid);
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(context.player());

            if(component.isPresent()) {
                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                    ItemStack stack = entry.getRight();
                    if(stack.getItem() instanceof SkillItem.ActiveSkillWithTarget && target != player) {
                        ActiveSkillCallback.EVENT.invoker().activeSkill(player, stack, target);
                        return;
                    }
                    if(stack.getItem() instanceof SkillItem.ActiveSkill && target == player) {
                        ActiveSkillCallback.EVENT.invoker().activeSkill(player, stack, player);
                        return;
                    }
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ShensuPayload.ID, (p, c) -> {
            PlayerEntity player =c.player();
            float speed = p.f();
            ItemStack stack = trinketItem(SkillCards.SHENSU, player);
            if (stack != null) {
                NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (component != null) {
                    NbtCompound nbt = component.copyNbt(); nbt.putFloat("speed", speed);
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                }
                //if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getFloat("speed") > 0) player.sendMessage(Text.literal("Speed: " + speed), true);
            }
        });
    }
}
