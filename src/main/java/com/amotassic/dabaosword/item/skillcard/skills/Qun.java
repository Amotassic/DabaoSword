package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.ICardEvent;
import com.amotassic.dabaosword.api.ReachDefend;
import com.amotassic.dabaosword.command.TriggerSkillCommand;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Qun {

    public static class Jijiu extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(Text.translatable("item.dabaosword.jijiu.tooltip"));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 10, isRedCard, new ItemStack(ModItems.PEACH));
            super.tick(stack, slot, entity);
        }
    }

    public static class Jiuchi extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(Text.translatable("item.dabaosword.jiuchi.tooltip"));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 10, isSpadeCard, new ItemStack(ModItems.JIU));
            super.tick(stack, slot, entity);
        }
    }

    public static class Jizhan extends SkillItem implements TriggerSkillCommand.CSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.jizhan.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.jizhan.tooltip2"));
        }

        private final MutableText JIZHAN_TEXT = Text.translatable("jizhan.text",
                Text.literal("更大").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword dabaosword:jizhan 1"))),
                Text.literal("更小").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dabaosword dabaosword:jizhan -1"))));

        @Override
        public int onDrawPhase(PlayerEntity player, ItemStack stack) {
            voice(player, stack);
            ItemStack last = newCard();
            give(player, last); //先让玩家摸一张牌，保存到lastCard
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("jizhan.draw", player.getDisplayName(), stack.toHoverableText(), last.toHoverableText(), Objects.requireNonNull(getRank(last)).rank), false));
            NbtCompound tag = stack.getOrCreateNbt();
            tag.putInt("lastCardRank", Objects.requireNonNull(getRank(last)).ordinal());
            stack.setNbt(tag);
            player.sendMessage(JIZHAN_TEXT, false);
            return -114;
        }

        @Override
        public void triggerSkill(LivingEntity entity, ItemStack stack, int value) {
            int last = stack.getOrCreateNbt().getInt("lastCardRank");
            if (last == -1 || !(entity instanceof PlayerEntity player)) return;
            ItemStack next = newCard();
            give(player, next); //又让玩家摸一张牌后，比较两张牌的点数，如果玩家选对了，就把新的牌保存到lastCard，否则关闭菜单
            player.getWorld().getPlayers().forEach(p -> p.sendMessage(Text.translatable("jizhan.draw", player.getDisplayName(), stack.toHoverableText(), next.toHoverableText(), Objects.requireNonNull(getRank(next)).rank), false));
            //下一张牌与上一张牌点数比较，有3种情况：更大返回1，更小返回-1，相等返回0
            int cmp = Integer.compare(Objects.requireNonNull(getRank(next)).ordinal(), last);
            //玩家选择只有两张情况：选更大返回1，选更小返回-1
            NbtCompound tag = stack.getOrCreateNbt();
            if (cmp == value) {
                tag.putInt("lastCardRank", Objects.requireNonNull(getRank(next)).ordinal());
                player.sendMessage(JIZHAN_TEXT, false);
            } else tag.putInt("lastCardRank", -1);
            stack.setNbt(tag);
        }
    }

    public static class Leiji extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.leiji.tooltip"));
        }

        @Override
        public void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            if (card.isOf(ModItems.SHAN)) {
                voice(user, skill);
                user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 10,3,false,false,false));
            }
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.NORMAL;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            return source.isOf(DamageTypes.LIGHTNING_BOLT) && source.getAttacker() == null;
        }
    }

    public static class Luanji extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luanji.tooltip"));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            super.tick(stack, slot, entity);
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(player) && getCD(stack) == 0) {
                ItemStack off = player.getOffHandStack();
                NbtCompound nbt = stack.getOrCreateNbt();
                Card.Suits firstSuit = null;
                if (nbt.contains("suit")) firstSuit = Card.Suits.get(nbt.getString("suit"));
                if (entity.getWorld().getTime() % 100 == 0 && firstSuit != null) {
                    player.sendMessage(Text.translatable("item.dabaosword.luanji.suit", stack.getName(), firstSuit.suit), true);
                }
                Card.Suits suit = getSuit(off);
                if (isCard(off) && suit != null) {
                    if (firstSuit == suit) { //如果记录花色和当前牌花色相同，就移除一张牌，获得万箭齐发，技能进入CD
                        nbt.remove("suit");
                        stack.setNbt(nbt);
                        setCD(stack, 15);
                        off.decrement(1);
                        give(player, new ItemStack(ModItems.WANJIAN));
                        voice(player, Sounds.LUANJI);
                        return;
                    }
                    if (firstSuit == null) { //如果没有记录花色，就移除一张牌，记录该花色
                        nbt.putString("suit", suit.suit);
                        stack.setNbt(nbt);
                        off.decrement(1);
                    }
                }
            }
        }
    }

    public static class Taoluan extends SkillItem.ActiveSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.taoluan.tooltip"));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            String[] used = stack.getOrCreateNbt().getString("used").split(";");
            if (used.length == 18) {
                user.sendMessage(Text.translatable("item.dabaosword.taoluan.fail").formatted(Formatting.RED), true);
                return;
            }
            if (user.getHealth() + 5 * countCard(user, canSaveDying) > 4.99) {

                ItemStack[] stacks = {new ItemStack(ModItems.THUNDER_SHA), new ItemStack(ModItems.FIRE_SHA), new ItemStack(ModItems.SHAN), new ItemStack(ModItems.PEACH), new ItemStack(ModItems.JIU), new ItemStack(ModItems.BINGLIANG_ITEM), new ItemStack(ModItems.TOO_HAPPY_ITEM), new ItemStack(ModItems.DISCARD), new ItemStack(ModItems.FIRE_ATTACK), new ItemStack(ModItems.JIEDAO), new ItemStack(ModItems.JUEDOU), new ItemStack(ModItems.NANMAN), new ItemStack(ModItems.STEAL), new ItemStack(ModItems.TAOYUAN), new ItemStack(ModItems.TIESUO), new ItemStack(ModItems.WANJIAN), new ItemStack(ModItems.WUXIE), new ItemStack(ModItems.WUZHONG)};
                Inventory inventory = new SimpleInventory(20);
                for (var stack1 : stacks) {
                    if (Arrays.stream(used).toList().contains(Registries.ITEM.getId(stack1.getItem()).getPath())) continue;
                    inventory.setStack(Arrays.stream(stacks).toList().indexOf(stack1), stack1);
                }
                inventory.setStack(18, stack);

                openSimpleMenu(user, user, inventory, Text.translatable("item.dabaosword.taoluan.screen"));
            }
            else {user.sendMessage(Text.translatable("item.dabaosword.taoluan.tip").formatted(Formatting.RED), true);}
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slotIndex) {
            if (selected.isEmpty()) return;
            give(player, selected);
            if (!player.isCreative()) {
                NbtCompound nbt = stack.getOrCreateNbt();
                String used = nbt.getString("used"); String item = Registries.ITEM.getId(selected.getItem()).getPath();
                used = used.isEmpty() ? item : used + ";" + item;
                nbt.putString("used", used); stack.setNbt(nbt);
                player.timeUntilRegen = 0;
                player.damage(player.getDamageSources().genericKill(), 4.99f);
            }
            voice(player, Sounds.TAOLUAN);
            closeGUI(player);
        }
    }

    public static class Weimu extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.weimu.tooltip"));
        }

        @Override
        public boolean canUseIfTargetHasSkill(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            if (isBlackCard.and(isArmoury).test(card)) {
                voice(target, skill); return false;
            }
            return true;
        }

        @Override
        public boolean canHurtByCard(LivingEntity entity, ItemStack skill, ItemStack card, DamageSource source) {
            if (card.isOf(ModItems.NANMAN)) {voice(entity, skill); return false;}
            return true;
        }
    }

    public static class Mashu extends SkillItem implements ReachDefend {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        @Override
        public int getExtraReach(PlayerEntity player, ItemStack stack) {return 1;}
    }

    public static class Feiying extends SkillItem implements ReachDefend {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        @Override
        public int getDefend(PlayerEntity player, ItemStack stack) {return 1;}
    }
}
