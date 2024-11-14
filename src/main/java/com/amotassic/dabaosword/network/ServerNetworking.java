package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.UUID;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ServerNetworking {
    public static Identifier ACTIVE_SKILL = new Identifier("dabaosword:active_skill");
    public static Identifier SHENSU = new Identifier("dabaosword:shensu_speed");
    public static Identifier SELECT_CARD = new Identifier("dabaosword:select_card");

    public static void registerActiveSkillPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ACTIVE_SKILL, ServerNetworking::receiveActiveSkillPacket);
        ServerPlayNetworking.registerGlobalReceiver(SHENSU, ServerNetworking::receiveShensuPacket);
        ServerPlayNetworking.registerGlobalReceiver(SELECT_CARD, ServerNetworking::selectCardPacket);
    }

    private static void receiveActiveSkillPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID uuid = buf.readUuid(); PlayerEntity target = server.getPlayerManager().getPlayer(uuid);
        server.execute(() -> {
            if (player.hasStatusEffect(ModItems.TIEJI)) {
                player.sendMessage(Text.translatable("effect.tieji.tip").formatted(Formatting.RED), true);
                return;
            }
            for(var stack : allTrinkets(player)) {
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
    }

    private static void receiveShensuPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        float speed = buf.readFloat();
        ItemStack stack = trinketItem(SkillCards.SHENSU, player);
        if (!stack.isEmpty()) {
            NbtCompound nbt = stack.getOrCreateNbt(); nbt.putFloat("speed", speed); stack.setNbt(nbt);
            //if (stack.getNbt() != null && stack.getNbt().getFloat("speed") > 0) player.sendMessage(Text.literal("Speed: " + speed), true);
        }
    }

    private static void selectCardPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int i = buf.readInt();
        if (i == 0) openInv(player, player, Text.translatable("key.dabaosword.select_card"), new ItemStack(ModItems.WANJIAN), true, false, false, 2);
        if (i == 1) openInv(player, player, Text.translatable("key.dabaosword.select_card"), new ItemStack(ModItems.SUNSHINE_SMILE), true, false, false, 3);
        if (i == 2 && hasTrinket(ModItems.CARD_PILE, player)) player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {}

            @Override
            public Text getDisplayName() {return Text.translatable("card_pile.title");}

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new PileScreenHandler(syncId, inv, new CardPileInventory(player));
            }
        });
        if (i == 3) {
            var pair = getDamage(player);
            if (pair != null) {
                //取消闪避后，先移除记录的伤害，给玩家一个CD防止闪触发
                Objects.requireNonNull(trinketItem(ModItems.CARD_PILE, player).getNbt()).remove("DamageDodged");
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
                player.damage(pair.getLeft().getLeft(), pair.getLeft().getRight());
                give(player, pair.getRight());
            }
        }
    }
}
