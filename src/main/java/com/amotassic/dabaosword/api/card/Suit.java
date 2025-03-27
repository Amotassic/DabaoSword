package com.amotassic.dabaosword.api.card;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum Suit {
    Heart(Text.translatable("suit.heart"), Formatting.RED),
    Diamond(Text.translatable("suit.diamond"), Formatting.RED),
    Spade(Text.translatable("suit.spade"), Formatting.WHITE),
    Club(Text.translatable("suit.club"), Formatting.WHITE),
    None(Text.translatable(" "), Formatting.WHITE);

    public final MutableText suit;
    public final Formatting color;

    Suit(MutableText suit, Formatting color) {
        this.suit = suit;
        this.color = color;
    }

    public static Suit fromNbt(NbtCompound nbt) {
        if (!nbt.contains("Suit")) return None;
        return Suit.valueOf(nbt.getString("Suit"));
    }
}
