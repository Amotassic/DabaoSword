package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public record SimplePayload(String name, String value) implements CustomPayload {
    public static final Id<SimplePayload> ID = new Id<>(Identifier.of("dabaosword:simple"));
    public static final PacketCodec<RegistryByteBuf, SimplePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SimplePayload::name,
            PacketCodecs.STRING, SimplePayload::value,
            SimplePayload::new
    );
    public static final String ACTIVE_SKILL =       "active_skill";
    public static final String SHENSU =             "shensu";
    public static final String QUICK_SWAP =         "quick_swap";
    public static final String CARD_PILE =          "card_pile";
    public static final String CANCEL_DODGE =       "cancel_dodge";
    public static final String VIEW_INFO =          "view_info";
    public static final String REPLACE_TRINKET =    "replace_trinket";
    private static final Map<String, BiConsumer<ServerPlayerEntity, String>> HANDLERS = Map.of(
            ACTIVE_SKILL,       SimplePayload::activeSkill,
            SHENSU,             SimplePayload::shensu,
            QUICK_SWAP,         SimplePayload::quickSwap,
            CARD_PILE,          SimplePayload::cardPile,
            CANCEL_DODGE,       SimplePayload::cancelDodge,
            VIEW_INFO,          SimplePayload::viewInfo,
            REPLACE_TRINKET,    SimplePayload::replaceTrinket
    );

    public static void execute(SimplePayload payload, ServerPlayNetworking.Context context) {
        if (HANDLERS.containsKey(payload.name)) HANDLERS.get(payload.name).accept(context.player(), payload.value);
    }

    public Id<? extends CustomPayload> getId() {return ID;}

    private static void activeSkill(ServerPlayerEntity player, String target) {
        if (player.hasStatusEffect(ModItems.TIEJI)) {
            player.sendMessage(Text.translatable("effect.tieji.tip").formatted(Formatting.RED), true);
            return;
        }
        int id = Integer.parseInt(target);
        LivingEntity entity = (LivingEntity) player.getEntityWorld().getEntityById(id);
        for (var skill : getSkillsMayUse(player)) if (player != entity && skill.activeSkill(player, skill, entity)) return;
        for (var skill : getSkillsMayUse(player)) if (skill.activeSkill(player, skill)) return;
    }

    private static void shensu(ServerPlayerEntity player, String value) {
        ItemStack stack = trinketItem(SkillCards.SHENSU, player);
        if (stack.isEmpty()) return;
        float speed = Float.parseFloat(value);
        NbtCompound nbt = getOrCreateNbt(stack); nbt.putFloat("speed", speed);
        setNbt(stack, nbt);
        //if (getOrCreateNbt(stack).getFloat("speed") > 0) player.sendMessage(Text.literal("Speed: " + speed), true);
    }

    private static void quickSwap(ServerPlayerEntity player, String value) {
        openInv(player, player, player, Text.translatable("key.dabaosword.select_card"), ItemStack.EMPTY, false, false, 3);
    }

    private static void cardPile(ServerPlayerEntity player, String value) {
        if (hasTrinket(ModItems.CARD_PILE, player))
            openScreen(player, Text.translatable("card_pile.title"), p -> new SimplePayload("", ""),
                    ((syncId, inv, p) -> new PileScreenHandler(syncId, inv)));
    }

    private static void cancelDodge(ServerPlayerEntity player, String value) {
        var pair = getDamage(player);
        if (pair == null) return;
        //取消闪避后，先移除记录的伤害，给玩家一个CD防止闪触发
        ItemStack stack = trinketItem(ModItems.CARD_PILE, player);
        NbtCompound nbt = getOrCreateNbt(stack); nbt.remove("DamageDodged");
        setNbt(stack, nbt);
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
        player.damage(world(player), pair.getLeft().getLeft(), pair.getLeft().getRight());
        give(player, pair.getRight());
    }

    private static void viewInfo(ServerPlayerEntity player, String value) {
        PlayerEntity target = getClosestEntity(player, PlayerEntity.class, 100, LivingEntity::isAlive);
        if (target != null) openFullInv(player, target, false);
    }

    private static void replaceTrinket(ServerPlayerEntity player, String slot) {
        replaceTrinketSlot(trinketsWithSlots(player, s -> s.isIn(Tags.SKILLS)), Integer.parseInt(slot));
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(String name) {sendToServer(name, "");}
    @Environment(EnvType.CLIENT)
    public static void sendToServer(String name, String value) {
        ClientPlayNetworking.send(new SimplePayload(name, value));
    }
}
