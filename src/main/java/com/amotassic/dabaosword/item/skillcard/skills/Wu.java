package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.item.tool.LetMeCCItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.api.CardEvents.cardToEquip;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.GREEN;

public class Wu {

    public static class Buqu extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int c = skill.getTag();
            if (Screen.hasShiftDown()) {
                tooltip.add(getTip("1", GREEN));
                tooltip.add(getTip("2", GREEN));
                tooltip.add(getTip("3", GREEN));
                tooltip.add(getTip("4", GREEN));
                tooltip.add(getTip("5", GREEN));
            } else {
                tooltip.add(Text.literal("创：" + c));
                tooltip.add(getTip(GREEN));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.SELF)
        public int onDying(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (user instanceof PlayerEntity player && player.isDead()) {
                int c = skill.getTag();
                voice(player, this);
                if (new Random().nextFloat() >= (float) c /13) {
                    player.sendMessage(Text.translatable("buqu.tip1", c + 1).formatted(GREEN));
                    skill.setTag(c + 1);
                    player.setHealth(1);
                } else player.sendMessage(Text.translatable("buqu.tip2").formatted(Formatting.RED));
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int die(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            int c = skill.getTag();
            if (c > 1) skill.setTag((c+1) / 2);
            return 0;
        }
    }

    public static class Fenyin extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(GREEN));}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCard(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getCard().toStack();
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
                if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
                else {
                    voice(user, this);
                    openInv(user, target, Text.translatable("gongxin.title"), skill.stack, false, false, false, 2);
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

    public static class Guose extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(getTip(GREEN));
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 15, isDiamondCard, ModItems.TOO_HAPPY_ITEM);
        }
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
                    user.damage(user.getDamageSources().genericKill(), 4.99f);
                }
                voice(user, this);
                return true;
            } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(Formatting.RED), true);}
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
            var targets = data.targets; var card = data.getCard();
            if (isSha.test(card.toStack()) && targets.contains(user) && hasCard(user, isCard)) {
                LivingEntity near = LetMeCCItem.getClosestEntity(user, LivingEntity.class, 50, entity -> entity != user && entity != target);
                if (near != null) {
                    targets.add(near);
                    while (targets.contains(user)) targets.remove(user);
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
                    if (target instanceof PlayerEntity pl) {give(pl, armor.copy()); armor.setCount(0);}
                    else {target.dropStack(armor.copy()); armor.setCount(0);}
                }
                voice(player, this);
                int i = target instanceof PlayerEntity ? 200 : 40;
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, i,0, false,false,true));
            }
        }
    }

    public static class Qixi extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(getTip(GREEN));
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 5, isBlackCard, ModItems.DISCARD);
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
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            int cd = skill.getCD();
            if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
            else {
                openInv(user, user, Text.translatable("zhiheng.title"), skill.stack, true, true, false, 2);
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
            } else user.sendMessage(Text.translatable("zhijian.fail").formatted(Formatting.RED), true);
            return false;
        }
    }
}
