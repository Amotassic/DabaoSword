package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.ICardEvent;
import com.amotassic.dabaosword.api.ReachDefend;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.event.CardEvents.cardMove;
import static com.amotassic.dabaosword.util.ModTools.*;

public class Shu {

    public static class Benxi extends SkillItem implements ReachDefend, ICardEvent {
        public Benxi(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            int benxi = getTag(stack);
            tooltip.add(Text.of("奔袭：" + benxi));
            tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public int getExtraReach(PlayerEntity player, ItemStack stack) {return getTag(stack);}

        @Override
        public void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            int benxi = getTag(skill);
            if (benxi < 5) {setTag(skill, benxi + 1); voice(user, skill);}
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {
            if (attacker instanceof PlayerEntity player && !player.getCommandTags().contains("benxi")) {
                int ben = getTag(stack);
                if (ben > 1) {
                    player.addCommandTag("benxi");
                    setTag(stack, ben - 2);
                    draw(player);
                    voice(player, Sounds.BENXI);
                }
            }
        }
    }

    public static class Huoji extends SkillItem {
        public Huoji(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.huoji.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 15, isRedCard, ModItems.FIRE_ATTACK);
            super.tick(stack, slot, entity);
        }
    }

    public static class Jizhi extends SkillItem implements ICardEvent {
        public Jizhi(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.jizhi.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            if (isArmoury.test(card)) {draw(user); voice(user, skill);}
        }
    }

    public static class Kanpo extends SkillItem {
        public Kanpo(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 10s" : "CD: 10s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.kanpo.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 10, isBlackCard, ModItems.WUXIE);
            super.tick(stack, slot, entity);
        }
    }

    public static class Kuanggu extends SkillItem {
        public Kuanggu(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(Text.translatable("item.dabaosword.kuanggu.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {
            if (attacker.hasStatusEffect(ModItems.COOLDOWN)) return;
            if (attacker.getMaxHealth() - attacker.getHealth()>=5) attacker.heal(5);
            else draw(attacker);
            voice(attacker, stack);
            attacker.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
        }
    }

    public static class Liegong extends SkillItem implements ReachDefend {
        public Liegong(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public int getExtraReach(PlayerEntity player, ItemStack stack) {
            return player.hasStatusEffect(ModItems.COOLDOWN) ? 0 : 13;
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                //烈弓：命中后给目标一个短暂的冷却效果，防止其自动触发闪
                target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity attacker) { //命中后加伤害，至少为5
                if (hasTrinket(SkillCards.LIEGONG, attacker) && !attacker.hasStatusEffect(ModItems.COOLDOWN)) {
                    float f = Math.max(13 - attacker.distanceTo(target), 5);
                    attacker.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, (int) (40 * f),0,false,false,true));
                    voice(attacker, Sounds.LIEGONG);
                    return new Pair<>(0f, f);
                }
            }
            return null;
        }
    }

    public static class Longdan extends SkillItem {
        public Longdan(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.longdan.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.longdan.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player && noTieji(entity)) {
                ItemStack stack1 = player.getOffHandStack(); ItemStack copy = stack1.copy();
                if (world.getTime() % 20 == 0 && isBasic.test(stack1)) {
                    stack1.decrement(1);
                    if (isSha.test(copy)) give(player, newCard(ModItems.SHAN));
                    if (copy.isOf(ModItems.SHAN)) give(player, newCard(p(ModItems.SHA).and(isRedCard)));
                    if (copy.isOf(ModItems.PEACH)) give(player, newCard(ModItems.JIU));
                    if (copy.isOf(ModItems.JIU)) give(player, newCard(ModItems.PEACH));
                    voice(player, Sounds.LONGDAN);
                }
            }
            super.tick(stack, slot, entity);
        }
    }

    public static class Rende extends SkillItem.ActiveSkillWithTarget {
        public Rende(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.rende.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.rende.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            openInv(user, target, Text.translatable("give_card.title", stack.getName()), stack, true, false, false, 2);
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slot) {
            voice(player, Sounds.RENDE);
            Text message = Text.translatable("give_card.tip", player.getDisplayName(), stack.toHoverableText(), target.getDisplayName(), selected.toHoverableText());
            target.sendMessage(message, false);
            player.sendMessage(message, false);
            cardMove(player, target, selected, 1, false, false);
            int cd = getCD(stack);
            if (player.getHealth() < player.getMaxHealth() && cd == 0 && new Random().nextFloat() < 0.5) {
                player.heal(5); voice(player, Sounds.RECOVER);
                player.sendMessage(Text.translatable("recover.tip").formatted(Formatting.GREEN), true);
                setCD(stack, 30);
            }
        }
    }

    public static class Tieji extends SkillItem {
        public Tieji(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            if (hasItem(player, isSha)) {
                voice(player, Sounds.TIEJI);
                target.addStatusEffect(new StatusEffectInstance(ModItems.TIEJI,200,0,false,true,true));
                if (new Random().nextFloat() < 0.75) target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }
    }

    public static class Wusheng extends SkillItem implements ReachDefend {
        public Wusheng(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.wusheng.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.wusheng.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public int getExtraReach(PlayerEntity player, ItemStack stack) {
            return isSha.test(player.getMainHandStack()) ? 13 : 0;
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 5, isRedCard, newCard(p(ModItems.SHA).and(isRedCard)));
            super.tick(stack, slot, entity);
        }
    }
}
