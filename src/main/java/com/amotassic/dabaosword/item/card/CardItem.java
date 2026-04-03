package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.*;

public abstract class CardItem extends Item {
    public CardItem(Properties settings) {super(settings);}

    public abstract int getType();

    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {}

    /**是否可以远距离使用*/
    public boolean rangedUse() {return false;}

    @Override @SuppressWarnings("deprecation")
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        addSRTip(c(stack), tooltip); addTip(stack, tooltip);
        tooltip.forEach(builder);
    }

    public void addTip(ItemStack stack, List<Component> tooltip) {

        if (stack.is(ModItems.SHAN)) {
            tooltip.add(Component.translatable("item.dabaosword.shan.tip").withStyle(BOLD));
            tooltip.add(getTip());
        }

        if (stack.is(ModItems.PEACH)) {
            tooltip.add(getTip("1", LIGHT_PURPLE));
            tooltip.add(getTip("2", LIGHT_PURPLE));
            tooltip.add(Component.translatable("item.dabaosword.recover.tip").withStyle(BOLD));
        }

        if (stack.is(ModItems.JIU)) {
            tooltip.add(getTip());
            tooltip.add(Component.translatable("item.dabaosword.recover.tip").withStyle(BOLD));
        }

        if (stack.is(ModItems.FIRE_ATTACK) || stack.is(ModItems.JIEDAO) || stack.is(ModItems.NANMAN) || stack.is(ModItems.TAOYUAN) || stack.is(ModItems.TIESUO) || stack.is(ModItems.JUEDOU)) tooltip.add(getTip());

        if (stack.is(ModItems.SHANDIAN_ITEM) || stack.is(ModItems.WUGU) || stack.is(ModItems.WUXIE) || stack.is(ModItems.STEAL) || stack.is(ModItems.WUZHONG) || stack.is(ModItems.DISCARD)) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2"));
        }

        if (stack.is(ModItems.BINGLIANG_ITEM)) {
            if (hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip(BLUE));
                tooltip.add(Component.translatable("dabaosword.shift_tip", Component.keybind("key.sneak")));
            }
        }

        if (stack.is(ModItems.TOO_HAPPY_ITEM)) {
            if (hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip(RED));
                tooltip.add(Component.translatable("dabaosword.shift_tip", Component.keybind("key.sneak")));
            }
        }

        if (stack.getItem() instanceof CardItem c && c.rangedUse()) {
            tooltip.add(Component.translatable("item.dabaosword.long_hand").withStyle(BOLD));
        }

        if (stack.is(ModItems.WANJIAN)) { //有大病的工具提示
            if (hasShiftDown()) {
                int i = (int) (System.currentTimeMillis() / 1000) % 7;
                switch (i) {
                    case 1 -> tooltip.add(getTip("1", AQUA));
                    case 2 -> tooltip.add(getTip("2", RED));
                    case 3 -> tooltip.add(getTip("3", GOLD));
                    case 4 -> tooltip.add(getTip("4", GREEN));
                    case 5 -> tooltip.add(getTip("5", DARK_PURPLE));
                    case 6 -> tooltip.add(getTip("6", YELLOW));
                    case 0 -> tooltip.add(getTip("7", BLUE));
                }
            } else {
                tooltip.add(getTip());
                tooltip.add(Component.translatable("item.dabaosword.wanjian.shift", Component.keybind("key.sneak")).withStyle(ITALIC));
            }
        }
    }
    public MutableComponent getTip(ChatFormatting... format) {return getTip("", format);}
    public MutableComponent getTip(String suffix, ChatFormatting... format) {
        return Component.translatable(getDescriptionId().replace("card.", "") + ".tooltip" + suffix).withStyle(format);
    }

    public final void addSRTip(Card card, List<Component> tooltip) {
        if (card.suit == Suit.None || card.rank == Rank.None) return;
        tooltip.add(Component.translatable("card.suit_and_rank", card.suit.suit, card.rank.rank).withStyle(card.suit.color));
    }

    public static void onUse(LivingEntity user, ItemStack stack, InteractionHand hand, LivingEntity... targets) {
        onUse(user, stack, hand, false, targets);
    }
    public static void onUse(LivingEntity user, ItemStack stack, InteractionHand hand, boolean noTarget, LivingEntity... targets) {
        onUse(user, stack, hand, noTarget, true, targets);
    }
    /**
     * @param hand 卡牌使用者的手，只影响玩家自动补牌功能，非玩家实体使用牌直接填null就行
     * @param noTarget 表示卡牌完全没有使用目标，类似于三国杀的“打出”。
     * @param consume 是否消耗卡牌。用于虚拟牌，如八卦阵视为使用的闪。
     * @param targets 卡牌的目标。对于我的mod中不便于选择目标的卡牌（比如火攻），需要将使用者填到目标中，否则卡牌不会执行任何效果。
     */
    public static void onUse(LivingEntity user, ItemStack stack, InteractionHand hand, boolean noTarget, boolean consume, LivingEntity... targets) {
        var card = c(stack); var cardData = d().cards(card, card.count);
        if (consume) CardEvents.cardUseAndDecrement(user, stack, hand);
        if (card.type != 2) voice(user, card.item());
        List<LivingEntity> owners = getSkillOwners(user);
        //触发卡牌使用事件
        owners.forEach(player -> getResult(Trigger.LOSE_CARD_USE, player, user, cardData));

        if (noTarget) return;
        var data = cardData.withTargets(targets);
        //触发修改卡牌目标的技能
        owners.forEach(player -> getResult(Trigger.ADD_TARGET, player, user, data));
        owners.forEach(player -> getResult(Trigger.DROP_TARGET, player, user, data));
        for (LivingEntity target : data.targets) {
            //当卡牌指定目标后，触发使用者的技能
            getResult(Trigger.SELECT_TARGET, user, target, cardData);
            //当有玩家成为卡牌目标后，触发玩家的技能
            owners.forEach(player -> getResult(Trigger.BECOME_TARGET, player, target, cardData));

            if (card.askForWuxie() && hasCard(target, p(ModItems.WUXIE))) { //如果目标有无懈可击，则使用无懈可击
                onUse(target, new ItemStack(ModItems.WUXIE), null, true);
                continue;
            }
            card.effect(user, card.toStack(), target);
        }
    }

    public static class Basic extends CardItem {
        public Basic(Properties settings) {super(settings);}

        public final int getType() {return Card.BASIC;}
    }

    public static class Armoury extends CardItem {
        public Armoury(Properties settings) {super(settings);}

        public final int getType() {return Card.ARMOURY;}

        public boolean askForWuxie() {return false;}
    }

    public static class Empty extends CardItem {
        public Empty(Properties settings) {super(settings);}

        public int getType() {return 114;}
    }
}
