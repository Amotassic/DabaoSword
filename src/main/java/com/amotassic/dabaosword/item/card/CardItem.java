package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.api.event.CardEvents.cardUsePre;
import static com.amotassic.dabaosword.util.ModTools.*;

public class CardItem extends Item implements Card {
    public CardItem() {super(new Settings());}

    @Override public Type getType() {return Type.ARMOURY;}

    public static class Wuzhong extends CardItem {
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && hand == Hand.MAIN_HAND) {
                if (cardUsePre(user, user.getMainHandStack(), null)) return TypedActionResult.success(user.getMainHandStack());
            }
            return super.use(world, user, hand);
        }

        @Override
        public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
            draw(user,2);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        addSRTip(stack, tooltip);

        if (stack.isOf(ModItems.SHAN)) {
            tooltip.add(Text.translatable("item.dabaosword.shan.tip").formatted(Formatting.BOLD));
            tooltip.add(getTip());
        }

        if (stack.isOf(ModItems.PEACH)) {
            tooltip.add(getTip("1").formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(getTip("2").formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(Formatting.BOLD));
        }

        if (stack.isOf(ModItems.JIU)) {
            tooltip.add(getTip());
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(Formatting.BOLD));
        }

        if (stack.isOf(ModItems.FIRE_ATTACK) || stack.isOf(ModItems.JIEDAO) || stack.isOf(ModItems.NANMAN) || stack.isOf(ModItems.TAOYUAN) || stack.isOf(ModItems.TIESUO) || stack.isOf(ModItems.JUEDOU)) tooltip.add(getTip());

        if (stack.isOf(ModItems.SHANDIAN_ITEM) || stack.isOf(ModItems.WUGU) || stack.isOf(ModItems.WUXIE) || stack.isOf(ModItems.STEAL) || stack.isOf(ModItems.WUZHONG) || stack.isOf(ModItems.DISCARD)) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2"));
        }

        if (stack.isOf(ModItems.DISCARD) || stack.isOf(ModItems.JUEDOU)) {
            tooltip.add(Text.translatable("item.dabaosword.long_hand").formatted(Formatting.BOLD));
        }

        if (stack.isOf(ModItems.BINGLIANG_ITEM)) {
            if (Screen.hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip().formatted(Formatting.BLUE));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        if (stack.isOf(ModItems.TOO_HAPPY_ITEM)) {
            if (Screen.hasShiftDown()) {
                tooltip.add(getTip("1"));
                tooltip.add(getTip("2"));
            } else {
                tooltip.add(getTip().formatted(Formatting.RED));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        if (stack.isOf(ModItems.WANJIAN)) { //有大病的工具提示
            if (Screen.hasShiftDown()) {
                int i = (int) (System.currentTimeMillis() / 1000) % 7;
                switch (i) {
                    case 1 -> tooltip.add(getTip("1").formatted(Formatting.AQUA));
                    case 2 -> tooltip.add(getTip("2").formatted(Formatting.RED));
                    case 3 -> tooltip.add(getTip("3").formatted(Formatting.GOLD));
                    case 4 -> tooltip.add(getTip("4").formatted(Formatting.GREEN));
                    case 5 -> tooltip.add(getTip("5").formatted(Formatting.DARK_PURPLE));
                    case 6 -> tooltip.add(getTip("6").formatted(Formatting.YELLOW));
                    case 0 -> tooltip.add(getTip("7").formatted(Formatting.BLUE));
                }
            } else {
                tooltip.add(getTip());
                tooltip.add(Text.translatable("item.dabaosword.wanjian.shift", Text.keybind("key.sneak")).formatted(Formatting.ITALIC));
            }
        }
    }

    public MutableText getTip() {return getTip("");}
    public MutableText getTip(String suffix) {
        return Text.translatable(getTranslationKey() + ".tooltip" + suffix);
    }

    public static void addSRTip(ItemStack stack, List<Text> tooltip) {
        var sr = getSuitAndRank(stack);
        if (sr != null) {
            Suits suit = sr.getLeft(); Ranks rank = sr.getRight();
            if (isRedCard.test(stack)) tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank).formatted(Formatting.RED));
            else tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank));
        }
    }
}
