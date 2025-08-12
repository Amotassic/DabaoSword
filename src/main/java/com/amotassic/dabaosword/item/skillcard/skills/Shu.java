package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.event.PlayerEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.RED;

public class Shu {

    public static class Benxi extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int benxi = skill.getTag();
            tooltip.add(Text.of("奔袭：" + benxi));
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override public boolean lockOn() {return true;}

        @Override
        public int getExtraReach(LivingEntity entity, Skill skill) {return skill.getTag();}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var benxi = skill.getTag();
            if (benxi < 5) {skill.setTag(benxi + 1); voice(user, this);}
            return 0;
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var benxi = skill.getTag();
            if (benxi > 1) {
                skill.setTag(benxi - 2);
                draw(user);
                voice(user, this);
            }
            return 0;
        }
    }

    public static class Huilei extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(RED));}

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int wu555(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            Entity killer = null; Entity attacker = data.source.getAttacker();
            if (attacker instanceof LivingEntity entity) killer = entity;
            else if (user.getPrimeAdversary() != null) killer = user.getPrimeAdversary();
            if (killer instanceof PlayerEntity pl) {
                voice(user, this);
                CardEvents.cardDiscard(pl, PlayerEvents.cardsToDrop(pl));
                for (var stack : pl.getInventory().main) {
                    if (stack.isEmpty()) continue;
                    give(pl, stack.copy(), 400);
                    stack.setCount(0);
                }
            }
            return 0;
        }
    }

    public static class Huoji extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip(RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isRedCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.FIRE_ATTACK;}
    }

    public static class Jizhi extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(RED));}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getFirst().toStack();
            if (isArmoury.test(card)) {draw(user); voice(user, this);}
            return 0;
        }
    }

    public static class Kanpo extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip(RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBlackCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.WUXIE;}
    }

    public static class Kuanggu extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(getTip(RED));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.DIRECT_ATTACKER)
        public int onAttack(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user.hasStatusEffect(ModItems.COOLDOWN)) return 0;
            if (user.getMaxHealth() - user.getHealth() >= 5) user.heal(5);
            else draw(user);
            voice(user, this);
            user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
            return 0;
        }
    }

    public static class Liegong extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public int getExtraReach(LivingEntity entity, Skill skill) {
            return entity.hasStatusEffect(ModItems.COOLDOWN) ? 0 : 13;
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                //烈弓：命中后给目标一个短暂的冷却效果，防止其自动触发闪
                target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int addDamage(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var adds = data.adds;
            if (!user.hasStatusEffect(ModItems.COOLDOWN)) {
                float f = Math.max(13 - user.distanceTo(target), 5);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, (int) (40 * f),0,false,false,true));
                voice(user, this);
                adds.add(f);
            }
            return 0;
        }
    }

    public static class Longdan extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBasic;}

        @Override
        public CardItem convert(ItemStack stack) {
            if (stack.isOf(ModItems.SHAN)) return ModItems.SHA;
            if (stack.isOf(ModItems.PEACH)) return ModItems.JIU;
            if (stack.isOf(ModItems.JIU)) return ModItems.PEACH;
            return ModItems.SHAN;
        }
    }

    public static class Paoxiao extends SkillItem {
        @Override public boolean lockOn() {return true;}

        @Override public boolean shouldTickUpdate() {return true;}

        @Override
        public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
            var modifiers = super.getModifiers(stack, slot, entity, uuid);
            int i = entity.getCommandTags().contains("duanchang") ? 0 : 1;
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(uuid, "paoxiao", i, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            return modifiers;
        }

        @SkillInfo(trigger = Trigger.SELECT_TARGET, relation = Relation.NOT_SELF)
        public int yuyin(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (isSha.test(data.getFirst().toStack())) voice(user, this);
            return 0;
        }
    }

    public static class Rende extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public void addScreenTip(Skill skill, List<Text> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 3, 4);
            super.addScreenTip(skill, tips);
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity entity) {
            if (entity instanceof PlayerEntity target && countCards(user) > 0) {
                openInv(user, user, target, Text.translatable("give_card.title", skill.toHoverableText()), skill.stack, false, false, 2);
                return true;
            } return false;
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {
            int count = handler.getSelectedCount();
            if (count == 0) return;
            voice(player, this);
            Text message = Text.translatable("give_card.tip", player.getDisplayName(), skill.toHoverableText(), target.getDisplayName(), count);
            target.sendMessage(message);
            player.sendMessage(message);
            CardEvents.cardMove(player, handler.toExData(), target);
            int cd = skill.getCD();
            if (player.getHealth() < player.getMaxHealth() && cd == 0 && new Random().nextFloat() < 0.5 * count) {
                player.heal(5); voice(player, "peach");
                player.sendMessage(Text.translatable("recover.tip").formatted(Formatting.GREEN), true);
                skill.setCD(30);
            }
        }
    }

    public static class Tieji extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            if (hasItem(player, isSha)) {
                voice(player, this);
                target.addStatusEffect(new StatusEffectInstance(ModItems.TIEJI,200,0,false,true,true));
                if (new Random().nextFloat() < 0.75) target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }
    }

    public static class Wusheng extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public int getExtraReach(LivingEntity entity, Skill skill) {
            return isSha.test(entity.getMainHandStack()) ? 13 : isSha.test(entity.getOffHandStack()) ? 13 : 0;
        }

        @Override public boolean chooseEquipment() {return true;}
        @Override public Predicate<ItemStack> getConvertFilter() {return isRedCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.SHA;}
    }
}
