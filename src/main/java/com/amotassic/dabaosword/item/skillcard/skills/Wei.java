package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.event.PlayerEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.amotassic.dabaosword.api.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.BLUE;

public class Wei {

    public static class Duanliang extends SkillItem {
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
                        attacker.timeUntilRegen = 0; attacker.damage(user.getDamageSources().mobAttack(user), f);
                    } else { //弃牌
                        if (attacker instanceof PlayerEntity target) { //如果来源是玩家则弃牌
                            List<ItemStack> candidate = getItems(target, isCard, true, false, true, true);
                            if (!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                Text message = Text.translatable("dabaosword.discard", user.getDisplayName(), target.getDisplayName(), chosen.toHoverableText());
                                if (user instanceof PlayerEntity player) player.sendMessage(message);
                                target.sendMessage(message);
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

        public static void gainMaxHp(LivingEntity entity, int hp) {
            Multimap<EntityAttribute, EntityAttributeModifier> maxHP = HashMultimap.create();
            final UUID HP_UUID = UUID.fromString("b29c34f3-1450-48ff-ab28-639647e11862");
            maxHP.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(
                    HP_UUID, "Max Hp", hp, EntityAttributeModifier.Operation.ADDITION));
            entity.getAttributes().addTemporaryModifiers(maxHP);
        }
    }

    public static class Jianxiong extends SkillItem {
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
                give(user, data.getCard().toStack().copyWithCount(1));
            }
            return 0;
        }
    }

    public static class Jueqing extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_LOWEST, relation = Relation.ATTACKER_SELF)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var amount = data.amount;
            target.damage(target.getDamageSources().genericKill(), Math.min(Math.max(7, target.getMaxHealth() / 3), amount));
            voice(user, this, 1);
            return 1;
        }
    }

    public static class Luoshen extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(BLUE));}

        @Override
        public int onDrawPhase(PlayerEntity player, Skill skill) {
            voice(player, this);
            while (true) {
                var card = newCard();
                player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("item.dabaosword.luoshen.result", player.getDisplayName(), card.toHoverableText())));
                if (isBlackCard.test(card)) give(player, card);
                else break;
            }
            return 0;
        }
    }

    public static class Luoyi extends SkillItem {
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
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && !user.isSneaking()) voice(user, this);
            return super.use(world, user, hand);
        }

        public static void gainStrength(LivingEntity entity, int value) {
            Multimap<EntityAttribute, EntityAttributeModifier> attack = HashMultimap.create();
            final UUID A_UUID = UUID.fromString("b29c34f3-1450-48ff-ab28-639647e11864");
            attack.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                    A_UUID, "Attack Damage", value, EntityAttributeModifier.Operation.ADDITION));
            entity.getAttributes().addTemporaryModifiers(attack);
        }

        private int getEmptyArmorSlot(LivingEntity entity) {
            int i = 0;
            for (var slot : entity.getArmorItems()) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Qice extends SkillItem {
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
                    Inventory inventory = new SimpleInventory(60);
                    for (var item : items) inventory.setStack(Arrays.stream(items).toList().indexOf(item) + 18, new ItemStack(item));
                    inventory.setStack(55, skill.stack);

                    openMenu(user, user, inventory, Text.translatable("item.dabaosword.qice.screen"));
                    return true;
                } else user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);
            } else user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
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
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.of("权：" + skill.getTag()));
            tooltip.add(getTip("1", BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
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
                    if (countAllCards(target) > 0) openInv(player, target, Text.translatable("dabaosword.discard.title", skill.toHoverableText()), skill.stack, false, true, false, 1);
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
            player.sendMessage(message);
            target.sendMessage(message);
            var data = d().cards(selected, 1, slot < 4);
            cardDiscard(target, data);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 12,0,false,false,true));
            closeGUI(player);
        }
    }

    public static class Shensu extends SkillItem {
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
            Multimap<EntityAttribute, EntityAttributeModifier> modifier = HashMultimap.create();
            final UUID uuid = UUID.fromString("7a0a818f-4487-4a98-91c4-14e75d6c1d7d");
            modifier.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                    uuid, "shensu", value, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            entity.getAttributes().addTemporaryModifiers(modifier);
        }

        private int getEmptySlots(PlayerEntity player) {
            int i = 0;
            for (var slot : player.getInventory().main) {if (slot.isEmpty()) i++;}
            return i;
        }
    }

    public static class Yiji extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 20s"));
            tooltip.add(getTip(BLUE));
            tooltip.add(getTip("2", BLUE));
        }

        @Override public boolean isActiveSkill() {return true;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onHurt(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (!user.hasStatusEffect(ModItems.COOLDOWN) && user.getHealth() <= 15) {
                draw(user, 2);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                skill.setTag(2);
                voice(user, this);
            }
            return 0;
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity entity) {
            if (entity instanceof PlayerEntity target) {
                int i = skill.getTag();
                if (i <= 0) return false;
                skill.setMaxSelect(i);
                openInv(user, target, Text.translatable("give_card.title", skill.toHoverableText()), skill.stack, true, false, false, 2);
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
            target.sendMessage(message);
            player.sendMessage(message);
            skill.setTag(skill.getTag() - count);
            cardMove(player, handler.toExData(), target);
        }
    }

    public static class Xingshang extends SkillItem {
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
