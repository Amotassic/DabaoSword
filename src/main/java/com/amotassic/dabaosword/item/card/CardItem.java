package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.*;

public abstract class CardItem extends Item {
    public CardItem(Settings settings) {super(settings);}

    public abstract int getType();

    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {}

    /**是否可以远距离使用*/
    public boolean rangedUse() {return false;}

    @Override @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        List<Text> tooltip = new ArrayList<>();
        addSRTip(c(stack), tooltip); addTip(stack, tooltip);
        tooltip.forEach(textConsumer);
    }

    public void addTip(ItemStack stack, List<Text> tooltip) {

        if (stack.isOf(ModItems.SHAN)) {
            tooltip.add(Text.translatable("item.dabaosword.shan.tip").formatted(BOLD));
            tooltip.add(getTip());
        }

        if (stack.isOf(ModItems.PEACH)) {
            tooltip.add(getTip("1", LIGHT_PURPLE));
            tooltip.add(getTip("2", LIGHT_PURPLE));
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(BOLD));
        }

        if (stack.isOf(ModItems.JIU)) {
            tooltip.add(getTip());
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(BOLD));
        }

        if (stack.isOf(ModItems.FIRE_ATTACK) || stack.isOf(ModItems.JIEDAO) || stack.isOf(ModItems.NANMAN) || stack.isOf(ModItems.TAOYUAN) || stack.isOf(ModItems.TIESUO) || stack.isOf(ModItems.JUEDOU)) tooltip.add(getTip());

        if (stack.isOf(ModItems.SHANDIAN_ITEM) || stack.isOf(ModItems.WUGU) || stack.isOf(ModItems.WUXIE) || stack.isOf(ModItems.STEAL) || stack.isOf(ModItems.WUZHONG) || stack.isOf(ModItems.DISCARD)) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2"));
        }

        if (stack.isOf(ModItems.BINGLIANG_ITEM)) {
            if (hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip(BLUE));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        if (stack.isOf(ModItems.TOO_HAPPY_ITEM)) {
            if (hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip(RED));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        if (stack.getItem() instanceof CardItem c && c.rangedUse()) {
            tooltip.add(Text.translatable("item.dabaosword.long_hand").formatted(BOLD));
        }

        if (stack.isOf(ModItems.WANJIAN)) { //有大病的工具提示
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
                tooltip.add(Text.translatable("item.dabaosword.wanjian.shift", Text.keybind("key.sneak")).formatted(ITALIC));
            }
        }
    }
    public MutableText getTip(Formatting... format) {return getTip("", format);}
    public MutableText getTip(String suffix, Formatting... format) {
        return Text.translatable(getTranslationKey() + ".tooltip" + suffix).formatted(format);
    }

    public final void addSRTip(Card card, List<Text> tooltip) {
        if (card.suit == Suit.None || card.rank == Rank.None) return;
        tooltip.add(Text.translatable("card.suit_and_rank", card.suit.suit, card.rank.rank).formatted(card.suit.color));
    }

    public static void onUse(LivingEntity user, ItemStack stack, Hand hand, LivingEntity... targets) {
        onUse(user, stack, hand, false, targets);
    }
    public static void onUse(LivingEntity user, ItemStack stack, Hand hand, boolean noTarget, LivingEntity... targets) {
        onUse(user, stack, hand, noTarget, true, targets);
    }
    /**
     * @param hand 卡牌使用者的手，只影响玩家自动补牌功能，非玩家实体使用牌直接填null就行
     * @param noTarget 表示卡牌完全没有使用目标，类似于三国杀的“打出”。
     * @param consume 是否消耗卡牌。用于虚拟牌，如八卦阵视为使用的闪。
     * @param targets 卡牌的目标。对于我的mod中不便于选择目标的卡牌（比如火攻），需要将使用者填到目标中，否则卡牌不会执行任何效果。
     */
    public static void onUse(LivingEntity user, ItemStack stack, Hand hand, boolean noTarget, boolean consume, LivingEntity... targets) {
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

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        addModel(stack, world);
    }

    public static void addModel(ItemStack stack, World world) {
        if (world.isClient()) return;
        if (stack.get(DataComponentTypes.CUSTOM_MODEL_DATA) != null) return;
        var card = c(stack);
        if (card.suit == Suit.None || card.rank == Rank.None) return;
        int s = card.suit.ordinal();
        int r = card.rank.ordinal() + 1;
        float f = 13 * s + r;
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(f), List.of(), List.of(), List.of()));
    }

    public static class Basic extends CardItem {
        public Basic(Settings settings) {super(settings);}

        public final int getType() {return Card.BASIC;}
    }

    public static class Armoury extends CardItem {
        public Armoury(Settings settings) {super(settings);}

        public final int getType() {return Card.ARMOURY;}

        public boolean askForWuxie() {return false;}
    }

    public static class Empty extends CardItem {
        public Empty(Settings settings) {super(settings);}

        public int getType() {return 114;}
    }
}
