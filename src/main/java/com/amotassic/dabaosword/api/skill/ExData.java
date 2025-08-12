package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.amotassic.dabaosword.util.ModTools.*;

/**
 * 不同的全局监听技能效果需要的参数不尽相同，为了能统一方法参数，故将除了技能本身外的参数封装在一个类中。
 * @author Amotassic
 */
public class ExData {
    /**目前仅用于存放卡牌指定的目标*/
    public List<LivingEntity> targets;
    public DamageSource source;
    public Float amount;
    /**当卡牌使用、弃置或移动时，存储来自手牌区的卡牌*/
    public final Map<Card, Integer> cards_from_inv = new HashMap<>();
    /**当卡牌弃置或移动时，存储来自装备区的卡牌*/
    public final Map<Card, Integer> cards_from_equ = new HashMap<>();
    /**修改伤害时，存放固定数值增减伤的数值*/
    public final List<Float> adds = new ArrayList<>();
    /**修改伤害时，存放倍率增伤（正值）和百分比减伤（负值）的数值*/
    public final List<Float> muls = new ArrayList<>();

    public ExData withTargets(LivingEntity... targets) {
        this.targets = toList(targets);
        return this;
    }

    /**从已有目标中彻底移除该实体*/
    public void removeTarget(LivingEntity entity) {
        if (targets == null) return;
        while (targets.contains(entity)) targets.remove(entity);
    }

    public ExData withDamage(DamageSource source, Float amount) {
        this.source = source;
        this.amount = amount;
        return this;
    }

    /**添加卡牌到data中，若卡牌来自于装备区，则第三个参数填true*/
    public ExData cards(ItemStack card, int count, boolean... fromEquip) {
        return cards(c(card), count, fromEquip);
    }

    public ExData cards(Card card, int count, boolean... fromEquip) {
        boolean bl = fromEquip.length > 0 && fromEquip[0];
        if (bl) cards_from_equ.put(card, count);
        else {
            for (var c : cards_from_inv.keySet()) {
                // 如果将要添加的卡牌已经存在了，但数量未达到该物品堆的数量，就增加该物品堆已计入的数量
                if (ItemStack.areEqual(c.toStack(), card.toStack())) {
                    Integer stored = cards_from_inv.get(c); int stackCount = c.count;
                    if (stored == stackCount) continue;

                    cards_from_inv.put(c, Math.min(stored + count, stackCount));
                    count -= stackCount - stored;
                    if (count <= 0) return this;
                }
            }
            cards_from_inv.put(card, count);
        }
        return this;
    }

    /**一定要在最后需要移除卡牌的时候才调用此方法，否则可能会出现找不到卡牌的情况！
     * <p>
     * 清除所有记录于此ExData的卡牌而不触发任何监听事件，如果卡牌是来自某个生物才会生效*/
    public void clearCards(LivingEntity cards_owner) {
        forEachCard(ALL, (c, i) -> {
            CardEvents.cardDecrement(cards_owner, c.origin(), i);
        });
    }

    /**获取第一张来自于手牌区的牌，一般用于使用卡牌时机，直接获取使用者使用的牌，其他情况不推荐使用此方法*/
    public Card getFirst(boolean... fromEquip) {
        boolean bl = fromEquip.length > 0 && fromEquip[0];
        if (bl && !cards_from_equ.isEmpty()) return cards_from_equ.keySet().iterator().next();
        else if (!cards_from_inv.isEmpty()) return cards_from_inv.keySet().iterator().next();
        return new Card(ModItems.EMPTY_CARD);
    }

    public boolean noCard() {return cards_from_inv.isEmpty() && cards_from_equ.isEmpty();}

    public static final int FROM_INV = 1, FROM_EQUIP = 2, ALL = 3;
    public void forEachCard(int from, BiFunction<Card, Integer, Boolean> func) {
        if (noCard()) return;
        if (from == 1 || from == 3) cards_from_inv.keySet().removeIf(c -> func.apply(c, cards_from_inv.get(c)));
        if (from == 2 || from == 3) cards_from_equ.keySet().removeIf(c -> func.apply(c, cards_from_equ.get(c)));
    }

    public void forEachCard(int from, BiConsumer<Card, Integer> consum) {
        if (noCard()) return;
        if (from == 1 || from == 3) cards_from_inv.forEach(consum);
        if (from == 2 || from == 3) cards_from_equ.forEach(consum);
    }
}
