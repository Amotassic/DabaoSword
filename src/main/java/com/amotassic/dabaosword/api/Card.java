package com.amotassic.dabaosword.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public interface Card {
    default void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {}

    /**若卡牌不立即消耗，就不会在{@link com.amotassic.dabaosword.api.event.CardEvents#cardUsePre(LivingEntity, ItemStack, LivingEntity)}中直接调用{@link com.amotassic.dabaosword.api.event.CardEvents#cardUsePost(LivingEntity, ItemStack, LivingEntity)}方法，因此需要在合适的时机调用后者*/
    default boolean notImmediatelyEffective() {return false;}

    Type getType();

    enum Type {
        BASIC,
        ARMOURY,
        EQUIPMENT
    }

    enum Suits {
        Heart ("♥"),
        Diamond ("♦"),
        Spade ("♠"),
        Club ("♣");

        public final String suit;

        Suits(String suit) {this.suit = suit;}

        public static Suits get(String suit) { //获取对应的花色，如果填的String不是这4个，则随机获取一个
            for (var s : Suits.values()) {
                if (Objects.equals(s.suit, suit)) return s;
            }
            return Arrays.stream(Suits.values()).toList().get(new Random().nextInt(4));
            //throw new IllegalArgumentException("No enum constant Suits." + suit);
        }
    }

    enum Ranks {
        Ace ("A"),
        Two ("2"),
        Three ("3"),
        Four ("4"),
        Five ("5"),
        Six ("6"),
        Seven ("7"),
        Eight ("8"),
        Nine ("9"),
        Ten ("10"),
        Jack ("J"),
        Queen ("Q"),
        King ("K");

        public final String rank;

        Ranks(String rank) {this.rank = rank;}

        public static Ranks get(String rank) { //同上
            for (var r : Ranks.values()) {
                if (Objects.equals(r.rank, rank)) return r;
            }
            return Arrays.stream(Ranks.values()).toList().get(new Random().nextInt(13));
            //throw new IllegalArgumentException("No enum constant Ranks." + rank);
        }
    }
}
