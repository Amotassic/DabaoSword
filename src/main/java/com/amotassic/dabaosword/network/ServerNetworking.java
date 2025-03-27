package com.amotassic.dabaosword.network;

import com.amotassic.dabaosword.command.InfoCommand;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ServerNetworking {

    public static void registerActiveSkill() {
        PayloadTypeRegistry.playC2S().register(ActiveSkillPayload.ID, ActiveSkillPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ShensuPayload.ID, ShensuPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(QuickSwapPayload.ID, QuickSwapPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ActiveSkillPayload.ID, (payload, context) -> {
            PlayerEntity player = context.player();
            if (player.hasStatusEffect(ModItems.TIEJI)) {
                player.sendMessage(Text.translatable("effect.tieji.tip").formatted(Formatting.RED), true);
                return;
            }
            int id = payload.id(); LivingEntity entity = (LivingEntity) player.getWorld().getEntityById(id);

            for (var skill : getSkillsMayUse(player)) if (player != entity && skill.activeSkill(player, skill, entity)) return;
            for (var skill : getSkillsMayUse(player)) if (skill.activeSkill(player, skill)) return;
        });

        ServerPlayNetworking.registerGlobalReceiver(ShensuPayload.ID, (p, c) -> {
            PlayerEntity player =c.player();
            float speed = p.f();
            ItemStack stack = trinketItem(SkillCards.SHENSU, player);
            if (!stack.isEmpty()) {
                NbtCompound nbt = getOrCreateNbt(stack); nbt.putFloat("speed", speed);
                setNbt(stack, nbt);
                //if (getOrCreateNbt(stack).getFloat("speed") > 0) player.sendMessage(Text.literal("Speed: " + speed), true);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(QuickSwapPayload.ID, (pl, c) -> {
            PlayerEntity player = c.player();
            int i = pl.id();
            var cards = new ItemStack(ModItems.WANJIAN); var items = new ItemStack(ModItems.SUNSHINE_SMILE);
            boolean bl = getCardPack(player).isEmpty(); //如果牌堆没有牌，会直接显示物品栏的牌，所以要判断一下
            if (i == 0) openInv(player, player, Text.translatable("key.dabaosword.select_card"), bl ? items : cards, true, false, false, 2);
            if (i == 1) openInv(player, player, Text.translatable("key.dabaosword.select_card"), items, true, false, false, 3);
            if (i == 2 && hasTrinket(ModItems.CARD_PILE, player)) player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {return new ActiveSkillPayload(0);}

                @Override
                public Text getDisplayName() {return Text.translatable("card_pile.title");}

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new PileScreenHandler(syncId, inv);
                }
            });
            if (i == 3) {
                var pair = getDamage(player);
                if (pair != null) {
                    //取消闪避后，先移除记录的伤害，给玩家一个CD防止闪触发
                    ItemStack stack = trinketItem(ModItems.CARD_PILE, player);
                    NbtCompound nbt = getOrCreateNbt(stack); nbt.remove("DamageDodged");
                    setNbt(stack, nbt);
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
                    player.damage(world(player), pair.getLeft().getLeft(), pair.getLeft().getRight());
                    give(player, pair.getRight());
                }
            }
            if (i == 9) {
                PlayerEntity target = getClosestEntity(player, PlayerEntity.class, 100, LivingEntity::isAlive);
                if (target != null) InfoCommand.openFullInv(player, target, false);
            }
        });
    }
}
