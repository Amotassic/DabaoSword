package com.amotassic.dabaosword.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.*;

public class TempInventory extends SimpleInventory {
    public final PlayerEntity user;
    public final LivingEntity owner;
    public final int type;
    public final Set<Integer> rowsToShow = new HashSet<>();

    public TempInventory(PlayerEntity user, ItemStack eventStack, List<ItemStack> stacks) {
        this(user, user, eventStack, 0, false, false);
        for (var stack : stacks) {
            int index = stacks.indexOf(stack);
            if (index < 81) setStack(index, stack);
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int index = i * 9 + j;
                if (!getStack(index).isEmpty()) {rowsToShow.add(i); break;}
            }
        }
    }

    /**
     @param equip: 是否显示装备牌
     @param armor: 是否显示玩家的盔甲
     @param type: 是否显示手牌。0：完全不显示；1：仅展示牌背；2：显示所有手牌；3：显示所有物品
     */
    public TempInventory(PlayerEntity user, LivingEntity owner, ItemStack eventStack, int type, boolean equip, boolean armor) {
        super(90);
        this.user = user; this.owner = owner;
        setStack(81, eventStack);
        this.type = type;

        if (equip) for (var stack : allTrinkets(owner)) {
            if (stack.isIn(Tags.WEAPON)) setStack(0, stack);
            if (stack.isIn(Tags.ARMOR)) setStack(1, stack);
            if (stack.isIn(Tags.DEFEND)) setStack(2, stack);
            if (stack.isIn(Tags.ATTACK)) setStack(3, stack);
        } //四件装备占1~4格

        var armors = getArmorItems(owner); int armorIndex = 0;
        if (armor) for (ItemStack stack : armors) {
            setStack(7 - armorIndex, stack); armorIndex++;
        } //4件盔甲占5~8格

        var off = owner.getOffHandStack(); //副手占9格
        if (shouldAdd(type, off)) setStack(8, off);

        boolean bl = type == 1;
        if (owner instanceof PlayerEntity pl) {
            var inv = pl.getInventory().getMainStacks(); //背包占2,3,4,5行
            for (var s : inv) if (shouldAdd(type, s)) setStack(9 + inv.indexOf(s), bl ? paibei(s.getCount()) : s);
            var pack = getCardPack(pl); var cards = pack.cards;
            if (!pack.isEmpty()) { //手牌背包的卡牌占6,7,8,9行
                for (var s : cards) if (shouldAdd(type, s)) setStack(45 + cards.indexOf(s), bl ? paibei(s.getCount()) : s);
            }
        } else { //不是玩家的话就只有主副手
            var main = owner.getMainHandStack();
            if (shouldAdd(type, main)) setStack(9, main);
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int index = i * 9 + j;
                if (!getStack(index).isEmpty()) {rowsToShow.add(i); break;}
            }
        }
    }

    boolean shouldAdd(int type, ItemStack stack) {
        if (type == 3) return true;
        return (type == 1 || type == 2) && isCard(stack);
    }
}
