package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.command.DabaoSwordCommand;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Qun {

    public static class Jijiu extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(getTip());
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 10, isRedCard, ModItems.PEACH);
        }
    }

    public static class Jiuchi extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(getTip());
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            viewAs(entity, skill, 10, isSpadeCard, ModItems.JIU);
        }
    }

    public static class Jizhan extends SkillItem implements DabaoSwordCommand.CSkill {
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2"));
        }

        private final MutableText JIZHAN_TEXT = Text.translatable("jizhan.text",
                Text.translatable("rank.higher").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword @s dabaosword:jizhan @s 1"))),
                Text.translatable("rank.lower").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword @s dabaosword:jizhan @s -1"))));

        @Override
        public int onDrawPhase(PlayerEntity player, Skill skill) {
            voice(player, this);
            ItemStack last = newCard();
            give(player, last); //先让玩家摸一张牌，保存到lastCard
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("jizhan.draw", player.getDisplayName(), skill.toHoverableText(), last.toHoverableText(), c(last).rank.rank), false));
            var tag = skill.getNbt();
            tag.putInt("lastCardRank", c(last).rank.ordinal());
            skill.setNbt(tag);
            player.sendMessage(JIZHAN_TEXT, false);
            return -114;
        }

        @Override
        public void triggerSkill(LivingEntity entity, Skill skill, LivingEntity target, int value) {
            int last = skill.getNbt().getInt("lastCardRank");
            if (last == -1 || !(entity instanceof PlayerEntity player)) return;
            ItemStack next = newCard();
            give(player, next); //又让玩家摸一张牌后，比较两张牌的点数，如果玩家选对了，就把新的牌保存到lastCard，否则关闭菜单
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("jizhan.draw", player.getDisplayName(), skill.toHoverableText(), next.toHoverableText(), c(next).rank.rank), false));
            //下一张牌与上一张牌点数比较，有3种情况：更大返回1，更小返回-1，相等返回0
            int cmp = Integer.compare(c(next).rank.ordinal(), last);
            //玩家选择只有两张情况：选更大返回1，选更小返回-1
            var tag = skill.getNbt();
            if (cmp == value) {
                tag.putInt("lastCardRank", c(next).rank.ordinal());
                player.sendMessage(JIZHAN_TEXT, false);
            } else tag.putInt("lastCardRank", -1);
            skill.setNbt(tag);
        }
    }

    public static class Leiji extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip());}

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useShan(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var card = data.getCard();
            if (card.isOf(ModItems.SHAN)) {
                voice(user, this);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 10,3,false,false,false));
            }
            return 0;
        }

        @SkillInfo(trigger = Trigger.CANCEL_DAMAGE_HIGH, relation = Relation.SELF)
        public int fanglei(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var source = data.source;
            if (source.isOf(DamageTypes.LIGHTNING_BOLT) && source.getAttacker() == null) return 1;
            return 0;
        }
    }

    public static class Luanji extends SkillItem {
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            int cd = skill.getCD();
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(getTip());
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && skill.getCD() == 0) {
                ItemStack off = player.getOffHandStack();
                var nbt = skill.getNbt();
                Suit firstSuit = null;
                if (nbt.contains("Suit")) firstSuit = Suit.fromNbt(nbt);
                if (entity.getWorld().getTime() % 100 == 0 && firstSuit != null) {
                    player.sendMessage(Text.translatable("item.dabaosword.luanji.suit", skill.toHoverableText(), firstSuit.suit), true);
                }
                Suit suit = c(off).suit;
                if (isCard(off) && suit != null) {
                    if (firstSuit == suit) { //如果记录花色和当前牌花色相同，就移除一张牌，获得万箭齐发，技能进入CD
                        nbt.remove("Suit");
                        skill.setNbt(nbt);
                        skill.setCD(15);
                        off.decrement(1);
                        give(player, newCard(ModItems.WANJIAN));
                        voice(player, this);
                        return;
                    }
                    if (firstSuit == null) { //如果没有记录花色，就移除一张牌，记录该花色
                        nbt.putString("Suit", suit.name());
                        skill.setNbt(nbt);
                        off.decrement(1);
                    }
                }
            }
        }
    }

    public static class Taoluan extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip());}

        @Override public boolean isActiveSkill() {return true;}

        @Override
        public boolean activeSkill(PlayerEntity user, Skill skill) {
            List<CardItem> items = ModItems.CARDS.stream().filter(i -> i.getType() != 2).toList();
            String[] used = skill.getNbt().getString("used").split(";");
            if (used.length == items.size()) {
                user.sendMessage(Text.translatable("item.dabaosword.taoluan.fail").formatted(Formatting.RED), true);
                return false;
            }
            if (user.getHealth() + 5 * countCard(user, canSaveDying) > 4.99) {

                Inventory inventory = new SimpleInventory(60);
                for (var item : items) {
                    if (Arrays.stream(used).toList().contains(Registries.ITEM.getId(item).getPath())) continue;
                    inventory.setStack(items.indexOf(item) + 18, new ItemStack(item));
                }
                inventory.setStack(55, skill.stack);

                openMenu(user, user, inventory, Text.translatable("item.dabaosword.taoluan.screen"));
                return true;
            } else user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);
            return false;
        }

        @Override
        public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
            var selected = handler.getStack(slot);
            give(player, selected);
            if (!player.isCreative()) {
                var nbt = skill.getNbt();
                String used = nbt.getString("used"); String item = Registries.ITEM.getId(selected.getItem()).getPath();
                used = used.isEmpty() ? item : used + ";" + item;
                nbt.putString("used", used); skill.setNbt(nbt);
                player.timeUntilRegen = 0;
                player.damage(player.getDamageSources().genericKill(), 4.99f);
            }
            voice(player, this);
            closeGUI(player);
        }

        @SkillInfo(trigger = Trigger.ON_DEATH, relation = Relation.SELF)
        public int refresh(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var nbt = skill.getNbt();
            nbt.remove("used"); skill.setNbt(nbt);
            return 0;
        }
    }

    public static class Weimu extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip());}

        @Override public boolean lockOn() {return true;}

        @SkillInfo(trigger = Trigger.DROP_TARGET, relation = Relation.ANY)
        public int weimu(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            //这里的user才是卡牌的目标，即技能的发动者
            var card = data.getCard();
            if (data.targets.contains(user) && isBlackCard.and(isArmoury).test(card.toStack())) {
                data.removeTarget(user);
                voice(user, this);
            }
            return 0;
        }
    }

    public static class Mashu extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        @Override public boolean lockOn() {return true;}

        public int getExtraReach(LivingEntity entity, Skill skill) {return 1;}
    }

    public static class Feiying extends SkillItem {
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        @Override public boolean lockOn() {return true;}

        public int getDefend(LivingEntity entity, Skill skill) {return 1;}
    }
}
