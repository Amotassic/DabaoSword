package com.amotassic.dabaosword.api.card;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Suit {
    Heart(Component.translatable("suit.heart"), ChatFormatting.RED),
    Diamond(Component.translatable("suit.diamond"), ChatFormatting.RED),
    Spade(Component.translatable("suit.spade"), ChatFormatting.WHITE),
    Club(Component.translatable("suit.club"), ChatFormatting.WHITE),
    None(Component.translatable(" "), ChatFormatting.WHITE);

    public final MutableComponent suit;
    public final ChatFormatting color;

    Suit(MutableComponent suit, ChatFormatting color) {
        this.suit = suit;
        this.color = color;
    }

    public static Suit fromNbt(CompoundTag nbt) {
        if (!nbt.contains("Suit")) return None;
        return Suit.valueOf(nbt.getString("Suit").orElseThrow());
    }
}
