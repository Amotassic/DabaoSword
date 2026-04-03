package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.event.PlayerEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.api.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.*;

public class Wei {

    public static class Chengxiang extends SkillItem {
        public Chengxiang(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override
        public void addScreenTip(Skill skill, List<Component> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 4);
            tips.add(Component.translatable("chengxiang.screen.tip").withStyle(LIGHT_PURPLE));
            super.addScreenTip(skill, tips);
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (!(user instanceof Player pl)) return 0;
            DamageSource source = data.source; Float amount = data.amount;
            if (source.is(ModDT.LOSEHP)) return 0;
            var nbt = skill.getNbt();
            float hurt = nbt.getFloat("hurt").orElse(0f); hurt += amount;
            if (hurt >= 7 && pl.isAlive()) {
                voice(pl, this);
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < 13; i++) {
                    if (i < 9) stacks.add(ItemStack.EMPTY);
                    else stacks.add(newCard());
                }
                pl.addEffect(new MobEffectInstance(ModItems.INVULNERABLE, 200));
                openMenu(pl, pl, skill.stack, stacks, skill.toHoverableText());
            }
            while (hurt >= 7) hurt -= 7;
            nbt.putFloat("hurt", hurt); skill.setNbt(nbt);
            return 0;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, Player player, Skill skill, Player target, int slot, int button, ContainerInput action) {
            int points = 0;
            for (var stack : handler.getSelected()) points += (c(stack).rank.ordinal() + 1);
            points += (c(handler.getStack(slot)).rank.ordinal() + 1);
            if (action == ContainerInput.PICKUP) {
                if (points <= 13 && button == 0) handler.addClick(slot);
                if (button == 1) handler.dropClick(slot);
            }
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, Player player, Skill skill, Player target) {
            if (handler.getSelectedCount() > 0) handler.toExData().forEachCard(3, (c, i) -> {
                give(player, c.toStack().copyWithCount(i));});
            else give(player, handler.getStack(9));
            player.removeEffect(ModItems.INVULNERABLE);
        }
    }

    public static class Daoshu extends SkillItem implements DabaoSwordCommand.CSkill {
        public Daoshu(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Component.literal(cd == 0 ? "CD: 60s" : "CD: 60s   left: "+ cd +"s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(Player user, Skill skill) {
            if (skill.getCD() == 0 && !user.entityTags().contains("seen_skill_tip")) {
                user.sendSystemMessage(Component.translatable("active_skill.use.tip", skill.toHoverableText(), activeSkillText(user, skill)).withStyle(GOLD));
                user.addTag("seen_skill_tip");
            } //主动技能使用提示
            return false;
        }

        @Override
        public boolean activeSkill(Player user, Skill skill, LivingEntity target) {
            user.addTag("seen_skill_tip");
            triggerSkill(user, skill, target, 0); return false;
        }

        @Override @SuppressWarnings("DuplicatedCode")
        public void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value) {
            if (skill.getCD() > 0 || entity == target) return;
            if (!(entity instanceof Player user) || !(target instanceof Player tar)) return;
            if (countCards(tar) < 1) {
                user.sendOverlayMessage(Component.translatable("daoshu.target.no_card").withStyle(RED));
                return;
            }
            if (value == 0) {
                user.sendSystemMessage(Component.translatable("select_a_suit", daoshuText(user, tar, Suit.Heart), daoshuText(user, tar, Suit.Diamond), daoshuText(user, tar, Suit.Spade), daoshuText(user, tar, Suit.Club)));
                return;
            }
            voice(user, this);
            List<ItemStack> items = getItems(tar, isCard, true, false, false, true);
            var card = items.get(new Random().nextInt(items.size())); var c = c(card);
            Component message = Component.translatable("dabaosword.steal", user.getDisplayName(), tar.getDisplayName(), card.getDisplayName());
            user.sendSystemMessage(message); tar.sendSystemMessage(message);
            cardMove(tar, d().cards(card, 1), user);
            if (c.suit.ordinal() + 1 == value) {
                // 防止触发杀和闪
                user.addTag("sha"); tar.addEffect(new MobEffectInstance(ModItems.COOLDOWN2, 1));
                tar.hurtServer(world(user), user.damageSources().mobAttack(user), 6);
            } else skill.setCD(60);
        }

        private MutableComponent daoshuText(Player user, Player target, Suit suit) { //四种花色的提示
            return suit.suit.withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/dabaosword " + user.getName().getString() + " dabaosword:daoshu " + target.getName().getString() + " " + (suit.ordinal() + 1)))).withStyle(suit.color);
        }
    }

    public static class Duanliang extends ConvertSkill {
        public Duanliang(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip(BLUE));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBlackCard.and(isArmoury.negate());}
        @Override public CardItem convert(ItemStack stack) {return ModItems.BINGLIANG_ITEM;}
    }

    public static class Fangzhu extends SkillItem {
        public Fangzhu(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(BLUE));}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var amount = data.amount;
            if (source.getEntity() instanceof LivingEntity attacker && user != attacker) {
                int i = attacker instanceof Player ? (int) (20 * amount + 60) : 300;
                attacker.addEffect(new MobEffectInstance(ModItems.TURNOVER, i));
                voice(user, this);
            }
            return 0;
        }
    }

    public static class Ganglie extends SkillItem {
        public Ganglie(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            var source = data.source; var amount = data.amount;
            if (source.getEntity() instanceof LivingEntity attacker && user != attacker) {
                voice(user, this);
                for (int i = 0; i < amount; i += 5) {
                    if (new Random().nextFloat() < 0.5) { //造成伤害
                        user.addTag("sha"); //以此造成伤害不自动触发杀
                        float f = i + 5 < amount ? 5 : amount - i;
                        attacker.invulnerableTime = 0;
                        attacker.hurtServer(world(entity), entity.damageSources().mobAttack(entity), f);
                    } else { //弃牌
                        if (attacker instanceof Player target) { //如果来源是玩家则弃牌
                            List<ItemStack> candidate = getItems(target, isCard, true, false, true, true);
                            if (!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                Component message = Component.translatable("dabaosword.discard", user.getDisplayName(), target.getDisplayName(), chosen.getDisplayName());
                                if (user instanceof Player player) player.sendSystemMessage(message);
                                target.sendSystemMessage(message);
                                var cData = d().cards(chosen, 1, isEquipped(attacker, s -> s.equals(chosen)));
                                cardDiscard(target, cData);
                            }
                        } else { //如果来源不是玩家则随机弃置它的主副手物品和装备
                            List<ItemStack> candidate = getItems(attacker, s -> !s.isEmpty(), true, false, true, false);
                            if (!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                if (isCard(chosen)) {
                                    var cData = d().cards(chosen, 1, isEquipped(attacker, s -> s.equals(chosen)));
                                    cardDiscard(attacker, cData);
                                }
                                else chosen.shrink(1);
                            }
                        }
                    }
                }
            }
            return 0;
        }
    }

    public static class Gongao extends SkillItem {
        public Gongao(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean lockOn() {return true;}

        @Override public boolean shouldTickUpdate() {return true;}

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.level().getGameTime() % 600 == 0 && entity instanceof Player player) { // 每30s触发扣体力上限
                int extraHP = skill.getTag();
                if (extraHP >= 5 && !player.isCreative() && !player.isSpectator()) {
                    draw(player, 2);
                    skill.setTag(extraHP - 5);
                    voice(player, "weizhong");
                }
            }
        }

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
            var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
            modifiers.put(Attributes.MAX_HEALTH, new AttributeModifier(slotIdentifier, s(stack).getTag(), AttributeModifier.Operation.ADD_VALUE));
            return modifiers;
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.KILLER)
        public int onKill(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            int extraHP = skill.getTag();
            int i = target instanceof Player ? 5 : target instanceof Monster ? 1 : 0;
            if (i > 0) {
                extraHP += i;
                user.heal(i);
                voice(user, this);
            }
            skill.setTag(extraHP);
            return 0;
        }
    }

    public static class Jianxiong extends SkillItem {
        public Jianxiong(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.literal("CD: 15s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source;
            if (source.getEntity() instanceof Entity && !user.hasEffect(ModItems.COOLDOWN)) {
                voice(user, this);
                draw(user);
                user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, 20 * 15,0,false,false,true));
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.HURT_BY_CARD, relation = Relation.SELF)
        public int hurtByCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (skill.getCD() == 0) {
                voice(user, this);
                skill.setCD(15);
                give(user, data.getFirst().toStack().copyWithCount(1));
            }
            return 0;
        }
    }

    public static class Jueqing extends SkillItem {
        public Jueqing(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_LOWEST, relation = Relation.ATTACKER_SELF)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var amount = data.amount;
            target.hurtServer(world(target), ModDT.loseHP(user), Math.min(Math.max(7, target.getMaxHealth() / 3), amount));
            voice(user, this, 1);
            return 1;
        }
    }

    public static class Luoshen extends SkillItem {
        public Luoshen(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(BLUE));}

        @Override
        public int onDrawPhase(Player player, Skill skill) {
            voice(player, this);
            while (true) {
                var card = newCard();
                player.level().players().forEach(p -> p.sendSystemMessage(Component.translatable("item.dabaosword.luoshen.result", player.getDisplayName(), card.getDisplayName())));
                if (isBlackCard.test(card)) give(player, card);
                else break;
            }
            return 0;
        }
    }

    public static class Luoyi extends SkillItem {
        public Luoyi(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(BLUE));}

        @Override public boolean lockOn() {return true;}

        @Override public boolean shouldTickUpdate() {return true;}

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
            var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
            int luoyi = entity.entityTags().contains("duanchang") ? 0 : getEmptyArmorSlot(entity) + 1;
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(slotIdentifier, luoyi, AttributeModifier.Operation.ADD_VALUE));
            return modifiers;
        }

        @Override
        public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
            if (!world.isClientSide() && !user.isShiftKeyDown()) voice(user, this);
            return super.use(world, user, hand);
        }

        private int getEmptyArmorSlot(LivingEntity entity) {
            int i = 0;
            for (var slot : getArmorItems(entity)) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Qice extends SkillItem {
        public Qice(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Component.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+ cd +"s"));
            tooltip.add(getTip(BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(Player user, Skill skill) {
            if (skill.getCD() == 0) {
                if (countCards(user) > 0) {

                    Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUGU, ModItems.WUZHONG};
                    var stacks = Arrays.stream(items).map(ItemStack::new).toList();

                    openMenu(user, user, skill.stack, stacks, Component.translatable("item.dabaosword.qice.screen"));
                    return true;
                } else user.sendOverlayMessage(Component.translatable("item.dabaosword.qice.tip").withStyle(RED));
            } else user.sendOverlayMessage(Component.translatable("dabaosword.cooldown").withStyle(RED));
            return false;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, Player player, Skill skill, Player target, int slot, int button, ContainerInput action) {
            var selected = handler.getStack(slot);
            if (!player.isCreative()) {
                while (countCards(player) > 0) {cardDecrement(player, getCard(player, isCard), 64);}
                skill.setCD(20);
            }
            give(player, selected);
            voice(player, this);
            closeGUI(player);
        }
    }

    public static class Qingguo extends ConvertSkill {
        public Qingguo(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip(BLUE));
        }

        @Override public Predicate<ItemStack> getConvertFilter() {return isBlackCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.SHAN;}
    }

    public static class Quanji extends SkillItem {
        public Quanji(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.literal("权：" + skill.getTag()));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override
        public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
            if (!world.isClientSide() && !user.isShiftKeyDown()) voice(user, "zili");
            return super.use(world, user, hand);
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.source.getEntity() instanceof LivingEntity) {
                skill.setTag(skill.getTag() + 1);
                voice(user, this);
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var adds = data.adds;
            int quan = skill.getTag();
            if (quan > 0) {
                if (quan > 4) draw(target);
                skill.setTag(quan/2);
                voice(user, "paiyi");
                adds.add((float) quan);
            }
            return 0;
        }
    }

    public static class Shanzhuan extends SkillItem {
        public Shanzhuan(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.literal("CD: 8s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        //擅专：我言既出，谁敢不从！
        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.ATTACKER)
        public int onHit(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            if (user instanceof Player player && !user.hasEffect(ModItems.COOLDOWN)) {
                if (entity instanceof Player target) {
                    if (countAllCards(target) > 0) openInv(player, target, target, Component.translatable("dabaosword.discard.title", skill.toHoverableText()), skill.stack, true, false, 1);
                } else {
                    voice(user, this);
                    if (new Random().nextFloat() < 0.5) {
                        entity.addEffect(new MobEffectInstance(ModItems.BINGLIANG, -1,1));
                    } else entity.addEffect(new MobEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                    user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                }
            }
            return 0;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, Player player, Skill skill, Player target, int slot, int button, ContainerInput action) {
            var selected = handler.getStack(slot);
            voice(player, this);
            if (isRedCard.test(selected)) target.addEffect(new MobEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
            else target.addEffect(new MobEffectInstance(ModItems.BINGLIANG, -1,1));
            Component message = Component.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.getDisplayName());
            player.sendSystemMessage(message);
            target.sendSystemMessage(message);
            var data = d().cards(selected, 1, slot < 4);
            cardDiscard(target, data);
            player.addEffect(new MobEffectInstance(ModItems.COOLDOWN, 20 * 12,0,false,false,true));
            closeGUI(player);
        }
    }

    public static class Shensu extends SkillItem {
        public Shensu(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var muls = data.muls;
            if (!user.hasEffect(ModItems.COOLDOWN)) {
                float walkSpeed = 4.317f;
                float speed = skill.getNbt().getFloat("speed").orElse(0f);
                if (speed > walkSpeed) {
                    float m = (speed - walkSpeed) / walkSpeed / 2;
                    user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, (int) (5 * 20 * m),0,false,false,true));
                    if (user instanceof Player player) player.sendOverlayMessage(Component.translatable("shensu.info", speed, m));
                    voice(user, this);
                    muls.add(m);
                }
            }
            return 0;
        }

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
            var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
            double d = 0;
            if (entity instanceof Player player && !player.entityTags().contains("duanchang")) {
                d = Math.min(getEmptySlots(player), 20d) / 40; //当空余20格时，获得最大加成0.5
            }
            modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(slotIdentifier, d, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            return modifiers;
        }

        private int getEmptySlots(Player player) {
            int i = 0;
            for (var slot : player.getInventory().getNonEquipmentItems()) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Yiji extends SkillItem {
        public Yiji(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.literal("CD: 20s"));
            tooltip.add(getTip(BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.source.is(ModDT.LOSEHP)) return 0;
            if (!user.hasEffect(ModItems.COOLDOWN) && user.getHealth() <= 15) {
                draw(user, 2);
                user.addEffect(new MobEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                skill.setTag(2);
                voice(user, this);
            }
            return 0;
        }

        @Override
        public void addScreenTip(Skill skill, List<Component> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 4, 5);
            super.addScreenTip(skill, tips);
        }

        @Override
        public boolean activeSkill(Player user, Skill skill, LivingEntity entity) {
            if (entity instanceof Player target) {
                int i = skill.getTag();
                if (i <= 0) return false;
                skill.setMaxSelect(i);
                openInv(user, user, target, Component.translatable("give_card.title", skill.toHoverableText()), skill.stack, false, false, 2);
                return true;
            }
            return false;
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, Player player, Skill skill, Player target) {
            int count = handler.getSelectedCount();
            if (count == 0) return;
            voice(player, this);
            Component message = Component.translatable("give_card.tip", player.getDisplayName(), skill.toHoverableText(), target.getDisplayName(), count);
            target.sendSystemMessage(message);
            player.sendSystemMessage(message);
            skill.setTag(skill.getTag() - count);
            cardMove(player, handler.toExData(), target);
        }
    }

    public static class Xingshang extends SkillItem {
        public Xingshang(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(BLUE));}

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.NOT_SELF)
        public int playerDie(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            if (entity instanceof Player target && user.distanceTo(target) <= 30) {
                voice(user, this);
                cardMove(target, PlayerEvents.cardsToDrop(target), user);
            }
            return 0;
        }
    }
}
