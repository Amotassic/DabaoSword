package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.api.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.*;

public class Wu {

    public static class Buqu extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            String c = skill.getNbt().getString("Chuang");
            if (!c.isEmpty()) tooltip.add(Text.literal("创：" + c));
            tooltip.add(getTip("1", GREEN));
            tooltip.add(getTip("2", GREEN));
        }

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onDying(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user instanceof PlayerEntity player && player.isDead()) {
                var nbt = skill.getNbt(); var chuang = nbt.getString("Chuang");
                if (chuang.length() > 36) return 0;
                var card = c(newCard());
                player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("buqu.tip1", player.getDisplayName(), skill.toHoverableText(), card.toStack().toHoverableText(), card.rank.rank)));
                if (chuang.isEmpty()) {
                    nbt.putString("Chuang", card.rank.rank);
                    voice(player, this);
                    player.setHealth(1);
                } else {
                    if (!chuang.contains(card.rank.rank)) {
                        nbt.putString("Chuang", chuang + ", " + card.rank.rank);
                        voice(player, this);
                        player.setHealth(1);
                    } else {
                        voice(player, "zhoutai");
                        if (chuang.split(", ").length < 3) player.sendMessage(Text.translatable("buqu.tip2").formatted(RED));
                    }
                }
                skill.setNbt(nbt);
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int die(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var nbt = skill.getNbt(); var chuang = nbt.getString("Chuang");
            if (chuang.length() > 6) {
                chuang = chuang.substring(6);
                nbt.putString("Chuang", chuang);
            } else nbt.remove("Chuang");
            skill.setNbt(nbt);
            return 0;
        }
    }

    public static class Fanjian extends SkillItem implements DabaoSwordCommand.CSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(getTip("1", GREEN));
            tooltip.add(getTip("2", GREEN));
        }
        private static final String FJ = "fanjian";

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            if (skill.getCD() == 0 && countCards(user) > 0 && !user.getCommandTags().contains("seen_skill_tip")) {
                user.sendMessage(Text.translatable("active_skill.use.tip", skill.toHoverableText(), activeSkillText(user, skill)).formatted(GOLD));
                user.addCommandTag("seen_skill_tip");
            } //主动技能使用提示
            return false;
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity target) {
            if (user == target) return false; //不能对自己犯贱
            if (skill.getCD() > 0) return false;
            if (target instanceof PlayerEntity player && countCards(user) > 0) { //有手牌才能发动
                voice(user, this);
                skill.setCD(30);
                addTag(player, FJ, user.getName().getString()); //标记玩家用于过时后确定目标
                player.sendMessage(Text.translatable("select_a_suit", fanjianText(user, player, Suit.Heart), fanjianText(user, player, Suit.Diamond), fanjianText(user, player, Suit.Spade), fanjianText(user, player, Suit.Club)));
                player.sendMessage(Text.translatable("fanjian.time.tip").formatted(BOLD));
                return true;
            }
            return false;
        }

        private MutableText fanjianText(PlayerEntity user, PlayerEntity player, Suit suit) { //四种花色的提示
            return suit.suit.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword " + user.getName().getString() + " dabaosword:fanjian " + player.getName().getString() + " " + (suit.ordinal() + 1)))).formatted(suit.color);
        }

        @Override
        public void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value) {
            if (entity instanceof PlayerEntity user) {
                if (countCards(user) == 0) {skill.setCD(0); return;} //因为技能延时，所以要判断使用者是否还有手牌，若不满足条件则返还技能CD
                if (value == 0) {this.activeSkill(user, skill, target); return;} //0表示对目标发动技能
                if (skill.getCD() < 21) return; //当目标选择一种花色后，技能CD一定大于21
                voice(user, this);
                List<ItemStack> items = getItems(user, isCard, true, false, false, true);
                var card = items.get(new Random().nextInt(items.size())); var c = c(card);
                Text message = Text.translatable("dabaosword.steal", target.getDisplayName(), user.getDisplayName(), card.toHoverableText());
                user.sendMessage(message); target.sendMessage(message);
                cardMove(user, d().cards(card, 1), target);
                if (c.suit.ordinal() + 1 != value) {
                    // 防止触发杀和闪
                    user.addCommandTag("sha"); target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1));
                    target.damage(damageSource(user, DamageTypes.MAGIC), 6);
                }
                skill.setCD(20);
            }
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity instanceof PlayerEntity player && player.getWorld().getTime() % 10 == 0) {
                int cd = skill.getCD();
                if (cd == 21 || cd == 0) { //目标超时未选择花色，寻找有标签的目标，随机选择一种花色
                    String tag = FJ + "_" + player.getName().getString();
                    Predicate<LivingEntity> p = e -> hasTag(e, tag);
                    var target = getClosestEntity(player, LivingEntity.class, 20, p);
                    if (target == null) target = player.getWorld().getPlayers().stream().filter(p).findFirst().orElse(null);
                    if (target == null) return;
                    if (cd == 21) this.triggerSkill(player, skill, target, new Random().nextInt(4) + 1);
                    removeTag(target, tag);
                }
            }
        }
    }

    public static class Fenyin extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getFirst().toStack();
            var nbt = skill.getNbt();
            int last = nbt.contains("fenyin") ? nbt.getInt("fenyin") : 0;
            int current = isRedCard.test(card) ? 1 : isBlackCard.test(card) ? 2 : 0;
            if (last != 0 && current != 0 && current != last) {draw(user); voice(user, this);}
            nbt.putInt("fenyin", current);
            skill.setNbt(nbt);
            return 0;
        }
    }

    public static class Gongxin extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(getTip(GREEN));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity entity) {
            if (entity instanceof PlayerEntity target && countCards(target) > 0) {
                int cd = skill.getCD();
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(RED), true);
                else {
                    voice(user, this);
                    openInv(user, target, target, Text.translatable("gongxin.title"), skill.stack, false, false, 2);
                    skill.setCD(30);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
            var selected = handler.getStack(slot);
            target.sendMessage(Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText()));
            var data = d().cards(selected, 1);
            cardDiscard(target, data);
            closeGUI(player);
        }
    }

    public static class Guose extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip(GREEN));
        }

        @Override public boolean chooseEquipment() {return true;}
        @Override public Predicate<ItemStack> getConvertFilter() {return isDiamondCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.TOO_HAPPY_ITEM;}
    }

    public static class Kurou extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            if (user.getHealth() + 5 * countCard(user, canSaveDying) > 4.99) {
                draw(user, 2);
                if (!user.isCreative()) {
                    user.timeUntilRegen = 0;
                    user.damage(ModDT.loseHP(user), 4.99f);
                }
                voice(user, this);
                return true;
            } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(RED), true);}
            return false;
        }
    }

    public static class Lianying extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world) {
                int cd = skill.getCD();
                if (world.getTime() % 20 == 0 && cd == 1) { //确保一秒内只触发一次
                    draw(entity);
                    voice(entity, this);
                }
            }
        }

        @SkillInfo(trigger = {Trigger.LOSE_CARD_USE, Trigger.LOSE_CARD_DISCARD, Trigger.LOSE_CARD_MOVE}, relation = Relation.SELF)
        public int loseCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user.isDead()) return 0;
            var lostCards = data.cards_from_inv;
            if (!lostCards.isEmpty() && countCards(user) == 0) skill.setCD(5);
            return 0;
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int die(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            skill.setCD(0);
            return 0;
        }
    }

    public static class Liuli extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.DROP_TARGET, relation = Relation.NOT_SELF)
        public int beiSha(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var targets = data.targets; var card = data.getFirst();
            if (isSha.test(card.toStack()) && targets.contains(user) && hasCard(user, isCard)) {
                LivingEntity near = getClosestEntity(user, LivingEntity.class, 50, e -> e != target);
                if (near != null) {
                    targets.add(near);
                    data.removeTarget(user);
                    voice(user, this);
                    var d = d().cards(getCard(user, isCard), 1);
                    cardDiscard(user, d);
                }
            }
            return 0;
        }
    }

    public static class Pojun extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(getTip(GREEN));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            //破军：攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                for (var armor : target.getArmorItems()) {
                    if (armor.isEmpty()) continue;
                    if (target instanceof PlayerEntity pl) {give(pl, armor.copy(), 100); armor.setCount(0);}
                    else {target.dropStack(armor.copy()); armor.setCount(0);}
                }
                voice(player, this);
                int i = target instanceof PlayerEntity ? 200 : 40;
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, i,0, false,false,true));
            }
        }
    }

    public static class Qixi extends ConvertSkill {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip(GREEN));
        }

        @Override public boolean chooseEquipment() {return true;}
        @Override public Predicate<ItemStack> getConvertFilter() {return isBlackCard;}
        @Override public CardItem convert(ItemStack stack) {return ModItems.DISCARD;}
    }

    public static class Shixin extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int fanghuo(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (data.source.isIn(DamageTypeTags.IS_FIRE)) {
                if (skill.getCD() == 0) {voice(user, this); skill.setCD(10);}
                return 1;
            }
            return 0;
        }
    }

    public static class Xiaoji extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @SkillInfo(trigger = {Trigger.LOSE_CARD_DISCARD, Trigger.LOSE_CARD_MOVE}, relation = Relation.SELF)
        public int loseEquip(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var equips = data.cards_from_equ;
            if (user.isAlive() && !equips.isEmpty()) {
                voice(user, this);
                draw(user, 2 * equips.size());
            }
            return 0;
        }
    }

    public static class Yingzi extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @Override
        public int onDrawPhase(PlayerEntity player, Skill skill) {
            voice(player, this);
            return 1;
        }
    }

    public static class Zhiheng extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(getTip("1", GREEN));
            tooltip.add(getTip("2", GREEN));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public void addScreenTip(Skill skill, List<Text> tips) {
            addPresetTips(skill, tips, 0, 1, 2, 3, 4);
            super.addScreenTip(skill, tips);
        }

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            if (countAllCards(user) == 0) return false;
            int cd = skill.getCD();
            if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(RED), true);
            else {
                openInv(user, user, user, Text.translatable("zhiheng.title"), skill.stack, true, false, 2);
                return true;
            }
            return false;
        }

        @Override
        public void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {
            var num = handler.getSelectedCount();
            if (num == 0) return;
            voice(player, this);
            var data = handler.toExData(); var count = countCards(player);
            if (count != 0 && count == data.cards_from_inv.size()) num += 1;
            cardDiscard(player, data);
            draw(player, num);
            skill.setCD(30);
        }
    }

    public static class Zhijian extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1", GREEN));
            tooltip.add(getTip("2", GREEN));
        }

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity target) {
            ItemStack itemStack = user.getMainHandStack();
            if (isEquipment.test(itemStack)) {
                var data = d().cards(itemStack, 1);
                cardToEquip(user, data, target);
                voice(user, this);
                draw(user);
                return true;
            } else user.sendMessage(Text.translatable("zhijian.fail").formatted(RED), true);
            return false;
        }
    }
}
