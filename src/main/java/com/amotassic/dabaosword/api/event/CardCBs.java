package com.amotassic.dabaosword.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 卡牌事件，用于监听卡牌的移动、丢弃、使用等事件
 * <p>
 * 注意：
 * 不要直接调用这个类里面的方法，而是通过使用{@link com.amotassic.dabaosword.util.ModTools}中已有的静态方法来调用监听器。
 * 因为事件内不会处理卡牌的减少，所有卡牌减少和相关的逻辑都在ModTools对应的方法中处理。
 */

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
         * 当卡牌使用时触发，用于判断是否能执行卡牌的效果。自动触发的卡牌不会触发该事件，因此还需要用{@link com.amotassic.dabaosword.util.ModTools#cardUsePost(LivingEntity, ItemStack, LivingEntity)}移除卡牌
         * @param stack 必须传入原始的stack
         * @return true 卡牌能生效，false 卡牌不能生效  注意：不论卡牌是否生效，都会触发{@link PostUse#cardUsePost(LivingEntity, ItemStack, LivingEntity)}
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
         * @param entity The entity who discards the card
         * @param stack The discarded stack
         */
        void cardDiscard(LivingEntity entity, ItemStack stack, int count, boolean fromEquip);
    }

    public interface Move {
        /**
         * Called after an entity's card(s) was moved to other player's inventory
         *
         * @param from The entity who lose the card
         * @param to The player who get the card
         * @param stack The moved stack with count (the next param)
         */
        void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, T type);
    }

    public enum T {
        INV_TO_INV,
        INV_TO_EQUIP,
        EQUIP_TO_INV,
        EQUIP_TO_EQUIP
    }

    public static Event<CanHurtByCard> CAN_HURT_BY_CARD = EventFactory.createArrayBacked(CanHurtByCard.class,
            listeners -> (entity, source, card) -> {
                for (CanHurtByCard listener: listeners){
                    return listener.canHurtByCard(entity, source, card);
                }
                return true;
            });

    public interface CanHurtByCard {
        boolean canHurtByCard(LivingEntity entity, DamageSource source, ItemStack card);
    }

    public static Event<HurtByCard> HURT_BY_CARD = EventFactory.createArrayBacked(HurtByCard.class,
            listeners -> (entity, source, card) -> {
                for (HurtByCard listener: listeners){
                    listener.hurtByCard(entity, source, card);
                }
            });

    public interface HurtByCard {
        /**主要用于受伤后获取造成伤害的卡牌*/
        void hurtByCard(LivingEntity entity, DamageSource source, ItemStack card);
    }
}
