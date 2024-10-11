package com.amotassic.dabaosword.api.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

public class CardCBs {

    public static Event<Move> MOVE = EventFactory.createArrayBacked(Move.class,
            listeners -> (from, to, stack, count, type) -> {
                for (Move listener: listeners){
                    listener.cardMove(from, to, stack, count, type);
                }
            });

    public static Event<Discard> DISCARD = EventFactory.createArrayBacked(Discard.class,
            listeners -> (player, stack, count, fromEquip) -> {
                for (Discard listener: listeners){
                    listener.cardDiscard(player, stack, count, fromEquip);
                }
            });

    public static Event<PostUse> USE_POST = EventFactory.createArrayBacked(PostUse.class,
            listeners -> (user, stack, target) -> {
                for (PostUse listener: listeners){
                    listener.cardUsePost(user, stack, target);
                }
            });

    public static Event<PreUse> USE_PRE = EventFactory.createArrayBacked(PreUse.class,
            listeners -> (user, stack, target) -> {
                for (PreUse listener: listeners){
                    return listener.cardUsePre(user, stack, target);
                }
                return false;
            });

    public interface PreUse {
        /**
         * 当卡牌使用时触发，用于判断是否能执行卡牌的效果，以及移除卡牌。自动触发的卡牌不会触发该事件，因此还需要手动移除卡牌
         * @param stack 必须传入原始的stack
         */
        boolean cardUsePre(LivingEntity user, ItemStack stack, @Nullable LivingEntity target);
    }

    public interface PostUse {
        /**
         * 当卡牌结算完成后触发
         * @param user The entity who used the card
         * @param stack 必须传入被使用的牌.copy()
         * @param target The target entity
         */
        void cardUsePost(LivingEntity user, ItemStack stack, @Nullable LivingEntity target);
    }

    public interface Discard {
        /**
         * Called after a player's card(s) was discarded
         *
         * @param player The player who discards the card
         * @param stack The discarded stack
         */
        void cardDiscard(PlayerEntity player, Pair<CardPileInventory, ItemStack> stack, int count, boolean fromEquip);
    }

    public interface Move {
        /**
         * Called after an entity's card(s) was moved to other player's inventory
         *
         * @param from The entity who lose the card
         * @param to The player who get the card
         * @param stack The moved stack with count (the next param)
         */
        void cardMove(LivingEntity from, PlayerEntity to, Pair<CardPileInventory, ItemStack> stack, int count, T type);
    }

    public enum T {
        INV_TO_INV,
        INV_TO_EQUIP,
        EQUIP_TO_INV,
        EQUIP_TO_EQUIP
    }
}
