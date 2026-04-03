package com.amotassic.dabaosword.api.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.amotassic.dabaosword.util.ModTools.getOrCreateNbt;
import static com.amotassic.dabaosword.util.ModTools.setNbt;

/**
 * 一个用于存放卡牌物品堆的类，包含了卡牌的类型、花色、点数、数量等信息，以及一些用于操作卡牌的方法。
 * 当然，这个类也可以用于存放其他物品而不会报错，只是没有卡牌应有的属性。
 * @author Amotassic
 */
public final class Card {
    private final ItemStack card;
    private final ItemStack copy;
    private final CardItem item;
    public final int type;
    public final Suit suit;
    public final Rank rank;
    public int count;
    public static final int BASIC = 0, ARMOURY = 1, EQUIPMENT = 2;

    public Card(CardItem item, Suit suit, Rank rank) {
        this.card = this.copy = ItemStack.EMPTY;
        this.item = item;
        this.type = item.getType();
        this.suit = suit;
        this.rank = rank;
        this.count = 1;
    }

    public Card(CardItem item) {this(item, Suit.None, Rank.None);}

    public Card(ItemStack card) {
        this.card = card; this.copy = card.copy();
        this.item = card.getItem() instanceof CardItem c ? c : ModItems.EMPTY_CARD;
        this.type = item.getType();
        this.count = card.getCount();
        var nbt = getOrCreateNbt(card);
        this.suit = count == 0 ? Suit.None : Suit.fromNbt(nbt);
        this.rank = count == 0 ? Rank.None : Rank.fromNbt(nbt);
    }

    public CardItem item() {return this.item;}

    public boolean isFromStack() {return !this.copy.isEmpty();}

    public ItemStack origin() {return card;}

    public ItemStack toStack() {
        if (isFromStack()) return copy.copy();
        var stack = new ItemStack(item, count); var nbt = new CompoundTag();
        if (suit != Suit.None) nbt.putString("Suit", suit.name());
        if (rank != Rank.None) nbt.putString("Rank", rank.rank);
        setNbt(stack, nbt);
        return stack;
    }

    public boolean askForWuxie() {
        if (item instanceof CardItem.Armoury a) return a.askForWuxie();
        return false;
    }

    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        this.item.effect(user, card, target);
    }

    public boolean isOf(Item item) {return this.item == item;}

    public Component getName() {return this.item.getName(card);}

    public String toString() {return card + ", suit=" + suit + ", rank=" + rank;}

}
