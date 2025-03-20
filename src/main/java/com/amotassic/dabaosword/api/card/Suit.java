package com.amotassic.dabaosword.api.card;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Formatting;

import java.util.Objects;

public enum Suit {
    Heart("♥", Formatting.RED),
    Diamond("♦", Formatting.RED),
    Spade("♠", Formatting.WHITE),
    Club("♣", Formatting.WHITE),
    None(" ", Formatting.WHITE);

    public final String suit;
    public final Formatting color;

    Suit(String suit, Formatting color) {
        this.suit = suit;
        this.color = color;
    }

    public static Suit fromString(String suit) {
        for (var s : Suit.values()) {
            if (Objects.equals(s.suit, suit)) return s;
        }
        return None;
    }

    public static Suit fromNbt(NbtCompound nbt) {
        if (!nbt.contains("Suit")) return None;
        return fromString(nbt.getString("Suit"));
    }
}
