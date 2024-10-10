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

    public interface PostUse {
        /**
         * Called after a player used a card to the target
         *
         * @param user The player who used the card
         * @param stack The used card stack 如果该卡牌不来自卡牌背包中，则{@link CardPileInventory}为null
         * @param target The target entity
         */
        void cardUsePost(PlayerEntity user, Pair<CardPileInventory, ItemStack> stack, @Nullable LivingEntity target);
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
