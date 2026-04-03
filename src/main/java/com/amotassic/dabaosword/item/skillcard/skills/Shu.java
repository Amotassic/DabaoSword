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
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

public class Shu {

    public static class Benxi extends SkillItem {
        public Benxi(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            int benxi = skill.getTag();
            tooltip.add(Component.literal("奔袭：" + benxi));
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
        public Huilei(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(RED));}

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int wu555(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            Entity killer = null; Entity attacker = data.source.getEntity();
            if (attacker instanceof LivingEntity entity) killer = entity;
            else if (user.getKillCredit() != null) killer = user.getKillCredit();
            if (killer instanceof Player pl) {
                voice(user, this);
                CardEvents.cardDiscard(pl, PlayerEvents.cardsToDrop(pl));
                for (var stack : pl.getInventory().getNonEquipmentItems()) {
                    if (stack.isEmpty()) continue;
                    give(pl, stack.copy(), 400);
                    stack.setCount(0);
                }
            }
            return 0;
        }
    }

    public static class Huoji extends ConvertSkill {
        public Huoji(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip(RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isRedCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.FIRE_ATTACK;}
    }

    public static class Jizhi extends SkillItem {
        public Jizhi(Properties settings) {super(settings);}
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(RED));}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getFirst().toStack();
            if (isArmoury.test(card)) {draw(user); voice(user, this);}
            return 0;
        }
    }

    public static class Kanpo extends ConvertSkill {
        public Kanpo(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip(RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBlackCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.WUXIE;}
    }

    public static class Kuanggu extends SkillItem {
        public Kuanggu(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.literal("CD: 8s"));
            tooltip.add(getTip(RED));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.DIRECT_ATTACKER)
        public int onAttack(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user.hasEffect(ModItems.COOLDOWN)) return 0;
            if (user.getMaxHealth() - user.getHealth() >= 5) user.heal(5);
            else draw(user);
            voice(user, this);
            user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
            return 0;
        }
    }

    public static class Liegong extends SkillItem {
        public Liegong(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public int getExtraReach(LivingEntity entity, Skill skill) {
            return entity.hasEffect(ModItems.COOLDOWN) ? 0 : 13;
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            if (!player.hasEffect(ModItems.COOLDOWN)) {
                //烈弓：命中后给目标一个短暂的冷却效果，防止其自动触发闪
                target.addEffect(new MobEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int addDamage(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var adds = data.adds;
            if (!user.hasEffect(ModItems.COOLDOWN)) {
                float f = Math.max(13 - user.distanceTo(target), 5);
                user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, (int) (40 * f),0,false,false,true));
                voice(user, this);
                adds.add(f);
            }
            return 0;
        }
    }

    public static class Longdan extends ConvertSkill {
        public Longdan(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBasic;}

        @Override
        public CardItem convert(ItemStack stack) {
            if (stack.is(ModItems.SHAN)) return ModItems.SHA;
            if (stack.is(ModItems.PEACH)) return ModItems.JIU;
            if (stack.is(ModItems.JIU)) return ModItems.PEACH;
            return ModItems.SHAN;
        }
    }

    public static class Paoxiao extends SkillItem {
        public Paoxiao(Properties settings) {super(settings);}

        @Override public boolean lockOn() {return true;}

        @Override public boolean shouldTickUpdate() {return true;}

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
            var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
            int i = entity.entityTags().contains("duanchang") ? 0 : 1;
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(slotIdentifier, i, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            return modifiers;
        }

        @SkillInfo(trigger = Trigger.SELECT_TARGET, relation = Relation.NOT_SELF)
        public int yuyin(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (isSha.test(data.getFirst().toStack())) voice(user, this);
            return 0;
        }
    }

    public static class Rende extends SkillItem {
        public Rende(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Component.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public void addScreenTip(Skill skill, List<Component> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 3, 4);
            super.addScreenTip(skill, tips);
        }

        @Override
        public boolean activeSkill(Player user, Skill skill, LivingEntity entity) {
            if (entity instanceof Player target && countCards(user) > 0) {
                openInv(user, user, target, Component.translatable("give_card.title", skill.toHoverableText()), skill.stack, false, false, 2);
                return true;
            } return false;
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, Player player, Skill skill, Player target) {
            int count = handler.getSelectedCount();
            if (count == 0) return;
            voice(player, this);
            Component message = Component.translatable("give_card.tip", player.getDisplayName(), skill.toHoverableText(), target.getDisplayName(), count);
            target.sendSystemMessage(message);
            player.sendSystemMessage(message);
            CardEvents.cardMove(player, handler.toExData(), target);
            int cd = skill.getCD();
            if (player.getHealth() < player.getMaxHealth() && cd == 0 && new Random().nextFloat() < 0.5 * count) {
                player.heal(5); voice(player, "peach");
                player.sendOverlayMessage(Component.translatable("recover.tip").withStyle(GREEN));
                skill.setCD(30);
            }
        }
    }

    public static class Tieji extends SkillItem {
        public Tieji(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            if (hasItem(player, isSha)) {
                voice(player, this);
                target.addEffect(new MobEffectInstance(ModItems.TIEJI,200,0,false,true,true));
                if (new Random().nextFloat() < 0.75) target.addEffect(new MobEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }
    }

    public static class Wusheng extends ConvertSkill {
        public Wusheng(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", RED));
            tooltip.add(getTip("2", RED));
        }

        @Override
        public int getExtraReach(LivingEntity entity, Skill skill) {
            return isSha.test(entity.getMainHandItem()) ? 13 : isSha.test(entity.getOffhandItem()) ? 13 : 0;
        }

        @Override public boolean chooseEquipment() {return true;}
        @Override public Predicate<ItemStack> getConvertFilter() {return isRedCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.SHA;}
    }
}
