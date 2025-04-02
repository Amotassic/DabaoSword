package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.event.PlayerEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

import static com.amotassic.dabaosword.api.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.*;

public class Wei {

    public static class Chengxiang extends SkillItem {
        public Chengxiang(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override
        public void addScreenTip(Skill skill, List<Text> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 4);
            tips.add(Text.translatable("chengxiang.screen.tip").formatted(LIGHT_PURPLE));
            super.addScreenTip(skill, tips);
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (!(user instanceof PlayerEntity pl)) return 0;
            DamageSource source = data.source; Float amount = data.amount;
            if (source.isOf(DamageTypes.GENERIC_KILL)) return 0;
            var nbt = skill.getNbt();
            float hurt = nbt.getFloat("hurt"); hurt += amount;
            if (hurt >= 7 && pl.isAlive()) {
                voice(pl, this);
                List<ItemStack> stacks = new ArrayList<>();
                for (int i = 0; i < 13; i++) {
                    if (i < 9) stacks.add(ItemStack.EMPTY);
                    else stacks.add(newCard());
                }
                pl.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 200));
                openMenu(pl, pl, skill.stack, stacks, skill.toHoverableText());
            }
            while (hurt >= 7) hurt -= 7;
            nbt.putFloat("hurt", hurt); skill.setNbt(nbt);
            return 0;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
            int points = 0;
            for (var stack : handler.getSelected()) points += (c(stack).rank.ordinal() + 1);
            points += (c(handler.getStack(slot)).rank.ordinal() + 1);
            if (action == SlotActionType.PICKUP) {
                if (points <= 13 && button == 0) handler.addClick(slot);
                if (button == 1) handler.dropClick(slot);
            }
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {
            if (handler.getSelectedCount() > 0) handler.toExData().forEachCard(3, (c, i) -> {
                give(player, c.toStack().copyWithCount(i));});
            else give(player, handler.getStack(9));
            player.removeStatusEffect(ModItems.INVULNERABLE);
        }
    }

    public static class Daoshu extends SkillItem implements DabaoSwordCommand.CSkill {
        public Daoshu(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 60s" : "CD: 60s   left: "+ cd +"s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            if (skill.getCD() == 0 && !user.getCommandTags().contains("seen_skill_tip")) {
                user.sendMessage(Text.translatable("active_skill.use.tip", skill.toHoverableText(), activeSkillText(user, skill)).formatted(GOLD), false);
                user.addCommandTag("seen_skill_tip");
            } //主动技能使用提示
            return false;
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity target) {
            user.addCommandTag("seen_skill_tip");
            triggerSkill(user, skill, target, 0); return false;
        }

        @Override @SuppressWarnings("DuplicatedCode")
        public void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value) {
            if (skill.getCD() > 0 || entity == target) return;
            if (!(entity instanceof PlayerEntity user) || !(target instanceof PlayerEntity tar)) return;
            if (countCards(tar) < 1) {
                user.sendMessage(Text.translatable("daoshu.target.no_card").formatted(RED), true);
                return;
            }
            if (value == 0) {
                user.sendMessage(Text.translatable("select_a_suit", daoshuText(user, tar, Suit.Heart), daoshuText(user, tar, Suit.Diamond), daoshuText(user, tar, Suit.Spade), daoshuText(user, tar, Suit.Club)), false);
                return;
            }
            voice(user, this);
            List<ItemStack> items = getItems(tar, isCard, true, false, false, true);
            var card = items.get(new Random().nextInt(items.size())); var c = c(card);
            Text message = Text.translatable("dabaosword.steal", user.getDisplayName(), tar.getDisplayName(), card.toHoverableText());
            user.sendMessage(message, false); tar.sendMessage(message, false);
            cardMove(tar, d().cards(card, 1), user);
            if (c.suit.ordinal() + 1 == value) {
                // 防止触发杀和闪
                user.addCommandTag("sha"); tar.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1));
                tar.damage(world(user), user.getDamageSources().mobAttack(user), 6);
            } else skill.setCD(60);
        }

        private MutableText daoshuText(PlayerEntity user, PlayerEntity target, Suit suit) { //四种花色的提示
            return suit.suit.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword " + user.getName().getString() + " dabaosword:daoshu " + target.getName().getString() + " " + (suit.ordinal() + 1)))).formatted(suit.color);
        }
    }

    public static class Duanliang extends SkillItem {
        public Duanliang(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(getTip(BLUE));
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 5, isBlackCard.and(isArmoury.negate()), ModItems.BINGLIANG_ITEM);
        }
    }

    public static class Fangzhu extends SkillItem {
        public Fangzhu(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(BLUE));}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source; var amount = data.amount;
            if (source.getAttacker() instanceof LivingEntity attacker && user != attacker) {
                int i = attacker instanceof PlayerEntity ? (int) (20 * amount + 60) : 300;
                attacker.addStatusEffect(new StatusEffectInstance(ModItems.TURNOVER, i));
                voice(user, this);
            }
            return 0;
        }
    }

    public static class Ganglie extends SkillItem {
        public Ganglie(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            var source = data.source; var amount = data.amount;
            if (source.getAttacker() instanceof LivingEntity attacker && user != attacker) {
                voice(user, this);
                for (int i = 0; i < amount; i += 5) {
                    if (new Random().nextFloat() < 0.5) { //造成伤害
                        user.addCommandTag("sha"); //以此造成伤害不自动触发杀
                        float f = i + 5 < amount ? 5 : amount - i;
                        attacker.timeUntilRegen = 0; attacker.damage(world(entity), entity.getDamageSources().mobAttack(entity), f);
                    } else { //弃牌
                        if (attacker instanceof PlayerEntity target) { //如果来源是玩家则弃牌
                            List<ItemStack> candidate = getItems(target, isCard, true, false, true, true);
                            if (!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                Text message = Text.translatable("dabaosword.discard", user.getDisplayName(), target.getDisplayName(), chosen.toHoverableText());
                                if (user instanceof PlayerEntity player) player.sendMessage(message, false);
                                target.sendMessage(message, false);
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
                                else chosen.decrement(1);
                            }
                        }
                    }
                }
            }
            return 0;
        }
    }

    public static class Gongao extends SkillItem {
        public Gongao(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean lockOn() {return true;}

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainMaxHp(entity, 0);
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.getWorld().isClient) return;
            int extraHP = skill.getTag();
            gainMaxHp(entity, extraHP);

            if (entity.getWorld().getTime() % 600 == 0) { // 每30s触发扣体力上限
                if (entity instanceof PlayerEntity player) {
                    if (extraHP >= 5 && !player.isCreative() && !player.isSpectator()) {
                        draw(player, 2);
                        skill.setTag(extraHP - 5);
                        voice(player, "weizhong");
                    }
                }
            }
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.KILLER)
        public int onKill(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            int extraHP = skill.getTag();
            if (target instanceof HostileEntity) {
                extraHP += 1;
                user.heal(1);
                voice(user, this);
            }
            if (target instanceof PlayerEntity) {
                extraHP += 5;
                user.heal(5);
                voice(user, this);
            }
            skill.setTag(extraHP);
            return 0;
        }

        private void gainMaxHp(LivingEntity entity, int value) {
            EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("max_hp"), value, EntityAttributeModifier.Operation.ADD_VALUE);
            Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.MAX_HEALTH)).updateModifier(AttributeModifier);
        }
    }

    public static class Jianxiong extends SkillItem {
        public Jianxiong(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 15s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source;
            if (source.getAttacker() instanceof Entity && !user.hasStatusEffect(ModItems.COOLDOWN)) {
                voice(user, this);
                draw(user);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 15,0,false,false,true));
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
        public Jueqing(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_LOWEST, relation = Relation.ATTACKER_SELF)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var amount = data.amount;
            target.damage(world(target), target.getDamageSources().genericKill(), Math.min(Math.max(7, target.getMaxHealth() / 3), amount));
            voice(user, this, 1);
            return 1;
        }
    }

    public static class Luoshen extends SkillItem {
        public Luoshen(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(BLUE));}

        @Override
        public int onDrawPhase(PlayerEntity player, Skill skill) {
            voice(player, this);
            while (true) {
                var card = newCard();
                player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("item.dabaosword.luoshen.result", player.getDisplayName(), card.toHoverableText()), false));
                if (isBlackCard.test(card)) give(player, card);
                else break;
            }
            return 0;
        }
    }

    public static class Luoyi extends SkillItem {
        public Luoyi(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(BLUE));}

        @Override public boolean lockOn() {return true;}

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainStrength(entity, getEmptyArmorSlot(entity) + 1);
        }

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainStrength(entity,0);
        }

        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && !user.isSneaking()) voice(user, this);
            return super.use(world, user, hand);
        }

        private void gainStrength(LivingEntity entity, int value) {
            EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("attack_damage"), value, EntityAttributeModifier.Operation.ADD_VALUE);
            Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.ATTACK_DAMAGE)).updateModifier(AttributeModifier);
        }

        private int getEmptyArmorSlot(LivingEntity entity) {
            int i = 0;
            for (var slot : entity.getArmorItems()) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Qice extends SkillItem {
        public Qice(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+ cd +"s"));
            tooltip.add(getTip(BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            if (skill.getCD() == 0) {
                if (countCards(user) > 0) {

                    Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.JUEDOU, ModItems.NANMAN, ModItems.STEAL, ModItems.TAOYUAN, ModItems.TIESUO, ModItems.WANJIAN, ModItems.WUXIE, ModItems.WUGU, ModItems.WUZHONG};
                    var stacks = Arrays.stream(items).map(ItemStack::new).toList();

                    openMenu(user, user, skill.stack, stacks, Text.translatable("item.dabaosword.qice.screen"));
                    return true;
                } else user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(RED), true);
            } else user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(RED), true);
            return false;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
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

    public static class Qingguo extends SkillItem {
        public Qingguo(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(getTip(BLUE));
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 5, isBlackCard, ModItems.SHAN);
        }
    }

    public static class Quanji extends SkillItem {
        public Quanji(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.of("权：" + skill.getTag()));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && !user.isSneaking()) voice(user, "zili");
            return super.use(world, user, hand);
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.source.getAttacker() instanceof LivingEntity) {
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
        public Shanzhuan(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        //擅专：我言既出，谁敢不从！
        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.ATTACKER)
        public int onHit(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            if (user instanceof PlayerEntity player && !user.hasStatusEffect(ModItems.COOLDOWN)) {
                if (entity instanceof PlayerEntity target) {
                    if (countAllCards(target) > 0) openInv(player, target, target, Text.translatable("dabaosword.discard.title", skill.toHoverableText()), skill.stack, true, false, 1);
                } else {
                    voice(user, this);
                    if (new Random().nextFloat() < 0.5) {
                        entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1,1));
                    } else entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                    user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                }
            }
            return 0;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
            var selected = handler.getStack(slot);
            voice(player, this);
            if (isRedCard.test(selected)) target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
            else target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1,1));
            Text message = Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText());
            player.sendMessage(message, false);
            target.sendMessage(message, false);
            var data = d().cards(selected, 1, slot < 4);
            cardDiscard(target, data);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 12,0,false,false,true));
            closeGUI(player);
        }
    }

    public static class Shensu extends SkillItem {
        public Shensu(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var muls = data.muls;
            if (!user.hasStatusEffect(ModItems.COOLDOWN)) {
                float walkSpeed = 4.317f;
                float speed = skill.getNbt().getFloat("speed");
                if (speed > walkSpeed) {
                    float m = (speed - walkSpeed) / walkSpeed / 2;
                    user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, (int) (5 * 20 * m),0,false,false,true));
                    if (user instanceof PlayerEntity player) player.sendMessage(Text.translatable("shensu.info", speed, m), true);
                    voice(user, this);
                    muls.add(m);
                }
            }
            return 0;
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
                double d = Math.min(getEmptySlots(player), 20d) / 40; //当空余20格时，获得最大加成0.5
                gainSpeed(player, Math.max(0, d));
            }
        }

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainSpeed(entity,0);
        }

        public static void gainSpeed(LivingEntity entity, double value) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(Identifier.of("shensu"), value, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED)).updateModifier(modifier);
        }

        private int getEmptySlots(PlayerEntity player) {
            int i = 0;
            for (var slot : player.getInventory().main) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Yiji extends SkillItem {
        public Yiji(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 20s"));
            tooltip.add(getTip(BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.source.isOf(DamageTypes.GENERIC_KILL)) return 0;
            if (!user.hasStatusEffect(ModItems.COOLDOWN) && user.getHealth() <= 15) {
                draw(user, 2);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                skill.setTag(2);
                voice(user, this);
            }
            return 0;
        }

        @Override
        public void addScreenTip(Skill skill, List<Text> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 4, 5);
            super.addScreenTip(skill, tips);
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity entity) {
            if (entity instanceof PlayerEntity target) {
                int i = skill.getTag();
                if (i <= 0) return false;
                skill.setMaxSelect(i);
                openInv(user, user, target, Text.translatable("give_card.title", skill.toHoverableText()), skill.stack, false, false, 2);
                return true;
            }
            return false;
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {
            int count = handler.getSelectedCount();
            if (count == 0) return;
            voice(player, this);
            Text message = Text.translatable("give_card.tip", player.getDisplayName(), skill.toHoverableText(), target.getDisplayName(), count);
            target.sendMessage(message, false);
            player.sendMessage(message, false);
            skill.setTag(skill.getTag() - count);
            cardMove(player, handler.toExData(), target);
        }
    }

    public static class Xingshang extends SkillItem {
        public Xingshang(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(BLUE));}

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.NOT_SELF)
        public int playerDie(LivingEntity user, LivingEntity entity, Skill skill, ExData data) {
            if (entity instanceof PlayerEntity target && user.distanceTo(target) <= 30) {
                voice(user, this);
                cardMove(target, PlayerEvents.cardsToDrop(target), user);
            }
            return 0;
        }
    }
}
