package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ServerNetworking {

    public static void registerActiveSkill() {
        PayloadTypeRegistry.playC2S().register(ActiveSkillPayload.ID, ActiveSkillPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShensuPayload.ID, ShensuPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ActiveSkillPayload.ID, (payload, context) -> {
            PlayerEntity player = context.player();
            if (player.hasStatusEffect(ModItems.TIEJI)) {
                player.sendMessage(Text.translatable("effect.tieji.tip").formatted(Formatting.RED), true);
                return;
            }
            UUID uuid = payload.uuid(); PlayerEntity target = context.player().server.getPlayerManager().getPlayer(uuid);
            for(var entry : allTrinkets(player)) {
                ItemStack stack = entry.getRight();
                if(stack.getItem() instanceof SkillItem.ActiveSkillWithTarget skill && target != player) {
                    skill.activeSkill(player, stack, target);
                    return;
                }
                if(stack.getItem() instanceof SkillItem.ActiveSkill skill && target == player) {
                    skill.activeSkill(player, stack, player);
                    return;
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
