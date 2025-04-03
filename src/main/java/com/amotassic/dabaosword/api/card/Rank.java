package com.amotassic.dabaosword.api.card;

import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public enum Rank {
    Ace("A"),
    Two("2"),
    Three("3"),
    Four("4"),
    Five("5"),
    Six("6"),
    Seven("7"),
    Eight("8"),
    Nine("9"),
    Ten("10"),
    Jack("J"),
    Queen("Q"),
    King("K"),
    None(" ");

    public final String rank;

    Rank(String rank) {
        this.rank = rank;
    }

    public static Rank fromString(String rank) {
        for (var r : Rank.values()) {
            if (Objects.equals(r.rank, rank)) return r;
        }
        return None;
    }

    public static Rank fromNbt(NbtCompound nbt) {
        if (!nbt.contains("Rank")) return None;
        return fromString(nbt.getString("Rank"));
    }
}
