package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class CardItem extends Item implements Card {
    public CardItem(Settings settings) {super(settings);}

    public static class Wuxie extends CardItem {
        public Wuxie(Settings settings) {super(settings);}

        @Override
        public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
            voice(user, Sounds.WUXIE);
            super.cardUse(user, stack, target);
        }
    }

    public static class Sha extends CardItem {
        public Sha(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            super.appendTooltip(stack, world, tooltip, tooltipContext);
            tooltip.add(Text.translatable("item.dabaosword.sha&shan.tooltip").formatted(Formatting.BOLD));

            if (stack.isOf(ModItems.SHA)) {
                tooltip.add(Text.translatable("item.dabaosword.sha.tooltip"));
            }
            if (stack.isOf(ModItems.FIRE_SHA)) {
                tooltip.add(Text.translatable("item.dabaosword.fire_sha.tooltip").formatted(Formatting.RED));
            }
            if (stack.isOf(ModItems.THUNDER_SHA)) {
                tooltip.add(Text.translatable("item.dabaosword.thunder_sha.tooltip").formatted(Formatting.BLUE));
            }
        }

        @Override
        public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
            World world = user.getWorld();
            user.addCommandTag("sha");
            if (stack.isOf(ModItems.SHA)) {
                voice(user, Sounds.SHA);
                if (!hasTrinket(ModItems.RATTAN_ARMOR, entity)) {
                    entity.timeUntilRegen = 0; entity.damage(user.getDamageSources().mobAttack(user), 5);
                } else voice(entity, Sounds.TENGJIA1);
            }
            if (stack.isOf(ModItems.FIRE_SHA)) {
                voice(user, Sounds.SHA_FIRE);
                entity.timeUntilRegen = 0; entity.setOnFireFor(5);
            }
            if (stack.isOf(ModItems.THUNDER_SHA)) {
                voice(user, Sounds.SHA_THUNDER);
                entity.timeUntilRegen = 0; entity.damage(user.getDamageSources().indirectMagic(user, user),5);
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                    lightningEntity.setCosmetic(true);
                }
                world.spawnEntity(lightningEntity);
            }
            super.cardUse(user, stack, entity);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        var sr = getSuitAndRank(stack);
        if (sr != null) {
            Suits suit = sr.getLeft();
            Ranks rank = sr.getRight();
            if (isRedCard.test(stack)) tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank).formatted(Formatting.RED));
            else tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank));
        }

        if (stack.getItem() == ModItems.WUXIE) {
            tooltip.add(Text.translatable("item.dabaosword.wuxie.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.wuxie.tooltip2"));
        }

        if (stack.getItem() == ModItems.ARROW_RAIN || stack.getItem() == ModItems.WANJIAN) {//有大病的工具提示
            if(Screen.hasShiftDown()){
                int i = (int) (System.currentTimeMillis() / 1000) % 7;
                if (i==0) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip7").formatted(Formatting.BLUE));}
                if (i==1) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip1").formatted(Formatting.AQUA));}
                if (i==2) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip2").formatted(Formatting.RED));}
                if (i==3) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip3").formatted(Formatting.GOLD));}
                if (i==4) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip4").formatted(Formatting.GREEN));}
                if (i==5) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip5").formatted(Formatting.DARK_PURPLE));}
                if (i==6) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip6").formatted(Formatting.YELLOW));}
            }else{
                tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.arrowrain.shift").formatted(Formatting.ITALIC));
            }
        }

        if (stack.getItem() == ModItems.BINGLIANG_ITEM) {
            if(Screen.hasShiftDown()){
                tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip1"));
                tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip2"));
            }else{
                tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip").formatted(Formatting.BLUE));
                tooltip.add(Text.translatable("dabaosword.shifttooltip"));
            }
        }

        if (stack.getItem() == ModItems.DISCARD) {
            tooltip.add(Text.translatable("item.dabaosword.discard.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.discard.tooltip2"));
            tooltip.add(Text.translatable("item.dabaosword.long_hand").formatted(Formatting.BOLD));
        }

        if (stack.getItem() == ModItems.FIRE_ATTACK) {
            tooltip.add(Text.translatable("item.dabaosword.huogong.tooltip"));
        }

        if (stack.getItem() == ModItems.JIEDAO) {
            tooltip.add(Text.translatable("item.dabaosword.jiedao.tooltip"));
        }

        if (stack.getItem() == ModItems.JIU) {
            tooltip.add(Text.translatable("item.dabaosword.jiu.tooltip"));
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(Formatting.BOLD));
        }

        if (stack.getItem() == ModItems.JUEDOU) {
            tooltip.add(Text.translatable("item.dabaosword.juedou.tooltip"));
            tooltip.add(Text.translatable("item.dabaosword.long_hand").formatted(Formatting.BOLD));
        }

        if (stack.getItem() == ModItems.NANMAN) {
            tooltip.add(Text.translatable("item.dabaosword.nanman.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.nanman.tooltip2"));
        }

        if (stack.getItem() == ModItems.PEACH) {
            tooltip.add(Text.translatable("item.dabaosword.peach.tooltip1").formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("item.dabaosword.peach.tooltip2").formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("item.dabaosword.recover.tip").formatted(Formatting.BOLD));
        }

        if (stack.getItem() == ModItems.SHAN) {
            tooltip.add(Text.translatable("item.dabaosword.sha&shan.tooltip").formatted(Formatting.BOLD));
            tooltip.add(Text.translatable("item.dabaosword.shan.tooltip"));
        }

        if (stack.getItem() == ModItems.STEAL) {
            tooltip.add(Text.translatable("item.dabaosword.steal.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.steal.tooltip2"));
        }

        if (stack.getItem() == ModItems.TAOYUAN) {
            tooltip.add(Text.translatable("item.dabaosword.taoyuan.tooltip"));
        }

        if (stack.getItem() == ModItems.TIESUO) {
            tooltip.add(Text.translatable("item.dabaosword.tiesuo.tooltip"));
        }

        if (stack.getItem() == ModItems.TOO_HAPPY_ITEM) {
            if(Screen.hasShiftDown()){
                tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip1"));
                tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip2"));
            }else{
                tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip").formatted(Formatting.RED));
                tooltip.add(Text.translatable("dabaosword.shifttooltip"));
            }
        }

        if (stack.getItem() == ModItems.WUZHONG) {
            tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.wuzhong.tooltip2"));
        }

        if (stack.getItem() == ModItems.GAIN_CARD) {
            tooltip.add(Text.translatable("item.dabaosword.gain_card.tooltip"));
        }
    }
}
