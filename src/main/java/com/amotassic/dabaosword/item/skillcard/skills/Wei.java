package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.ICardEvent;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Sounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.*;

import static com.amotassic.dabaosword.api.event.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;

public class Wei {

    public static class Duanliang extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.duanliang.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 5, isBlackCard.and(isArmoury.negate()), new ItemStack(ModItems.BINGLIANG_ITEM));
            super.tick(stack, slot, entity);
        }
    }

    public static class Fangzhu extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.fangzhu.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity attacker && entity != attacker) {
                int i = attacker instanceof PlayerEntity ? (int) (20 * amount + 60) : 300;
                attacker.addStatusEffect(new StatusEffectInstance(ModItems.TURNOVER, i));
                voice(entity, Sounds.FANGZHU);
            }
        }
    }

    public static class Ganglie extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.ganglie.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.ganglie.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity attacker && entity != attacker) {
                voice(entity, Sounds.GANGLIE);
                for (int i = 0; i < amount; i += 5) {//造成伤害
                    if (new Random().nextFloat() < 0.5) {
                        entity.addCommandTag("sha");//以此造成伤害不自动触发杀
                        float f = i + 5 < amount ? 5 : amount - i;
                        attacker.timeUntilRegen = 0; attacker.damage(entity.getDamageSources().mobAttack(entity), f);
                    } else {//弃牌
                        if (attacker instanceof PlayerEntity target) { //如果来源是玩家则弃牌
                            List<ItemStack> candidate = getItems(target, isCard, true, false, true, true);
                            if(!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                Text message = Text.translatable("dabaosword.discard", entity.getDisplayName(), target.getDisplayName(), chosen.toHoverableText());
                                if (entity instanceof PlayerEntity player) player.sendMessage(message);
                                target.sendMessage(message);
                                cardDiscard(target, chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                            }
                        } else { //如果来源不是玩家则随机弃置它的主副手物品和装备
                            List<ItemStack> candidate = getItems(attacker, s -> !s.isEmpty(), true, false, true, false);
                            if(!candidate.isEmpty()) {
                                ItemStack chosen = candidate.get(new Random().nextInt(candidate.size()));
                                if (isCard(chosen)) cardDiscard(entity, chosen, 1, isEquipped(entity, s -> s.equals(chosen)));
                                else chosen.decrement(1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Gongao extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.gongao.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.gongao.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainMaxHp(entity, 0);
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) {
                int extraHP = getTag(stack);

                gainMaxHp(entity, extraHP);
                if (entity.getWorld().getTime() % 600 == 0) { // 每30s触发扣体力上限
                    if (entity instanceof PlayerEntity player) {
                        if (extraHP >= 5 && !player.isCreative() && !player.isSpectator()) {
                            draw(player, 2);
                            setTag(stack, extraHP - 5);
                            voice(player, Sounds.WEIZHONG);
                        }
                    }
                }
            }
            super.tick(stack, slot, entity);
        }

        @Override
        public void postDamage(ItemStack stack, LivingEntity target, LivingEntity player, float amount) {
            if (target.isDead()) {
                if (target instanceof HostileEntity) {
                    int extraHP = getTag(stack);
                    setTag(stack, extraHP +1);
                    player.heal(1);
                    voice(player, Sounds.GONGAO);
                }
                if (target instanceof PlayerEntity) {
                    int extraHP = getTag(stack);
                    setTag(stack, extraHP + 5);
                    player.heal(5);
                    voice(player, Sounds.GONGAO);
                }
            }
        }

        public static void gainMaxHp(LivingEntity entity, int hp) {
            Multimap<EntityAttribute, EntityAttributeModifier> maxHP = HashMultimap.create();
            final UUID HP_UUID = UUID.fromString("b29c34f3-1450-48ff-ab28-639647e11862");
            maxHP.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(
                    HP_UUID, "Max Hp", hp, EntityAttributeModifier.Operation.ADDITION));
            entity.getAttributes().addTemporaryModifiers(maxHP);
        }
    }

    public static class Jianxiong extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 15s"));
            tooltip.add(Text.translatable("item.dabaosword.jianxiong.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jianxiong.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (source.getAttacker() instanceof Entity && !entity.hasStatusEffect(ModItems.COOLDOWN)) {
                voice(entity, stack);
                draw(entity);
                entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 15,0,false,false,true));
            }
        }

        @Override
        public void onHurtByCard(LivingEntity entity, ItemStack skill, ItemStack card) {
            if (getCD(skill) == 0) {
                voice(entity, skill);
                setCD(skill, 15);
                give(entity, card.copyWithCount(1));
            }
        }
    }

    public static class Jueqing extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.LOWEST;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity attacker && hasTrinket(SkillCards.JUEQING, attacker)) {
                target.damage(target.getDamageSources().genericKill(), Math.min(Math.max(7, target.getMaxHealth() / 3), amount));
                voice(attacker, Sounds.JUEQING, 1);
                return true;
            }
            return false;
        }
    }

    public static class Luoshen extends SkillItem.ActiveSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luoshen.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            int cd = getCD(stack);
            if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
            else {
                voice(user, Sounds.LUOSHEN);
                if (new Random().nextFloat() < 0.5) {
                    draw(user);
                    user.sendMessage(Text.translatable("item.dabaosword.luoshen.win").formatted(Formatting.GREEN), true);
                } else {
                    setCD(stack, 30);
                    user.sendMessage(Text.translatable("item.dabaosword.luoshen.lose").formatted(Formatting.RED), true);
                }
            }
        }
    }

    public static class Luoyi extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.luoyi.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainStrength(entity, getEmptyArmorSlot(entity) + 1);
        }

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainStrength(entity,0);
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && !user.isSneaking()) voice(user, Sounds.LUOYI);
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

    public static class Qice extends SkillItem.ActiveSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.qice.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            int cd = getCD(stack);
            if (countCards(user) > 0) {
                if (cd == 0) {

                    ItemStack[] stacks = {new ItemStack(ModItems.BINGLIANG_ITEM), new ItemStack(ModItems.TOO_HAPPY_ITEM), new ItemStack(ModItems.DISCARD), new ItemStack(ModItems.FIRE_ATTACK), new ItemStack(ModItems.JIEDAO), new ItemStack(ModItems.JUEDOU), new ItemStack(ModItems.NANMAN), new ItemStack(ModItems.STEAL), new ItemStack(ModItems.TAOYUAN), new ItemStack(ModItems.TIESUO), new ItemStack(ModItems.WANJIAN), new ItemStack(ModItems.WUXIE), new ItemStack(ModItems.WUZHONG)};
                    Inventory inventory = new SimpleInventory(20);
                    for (var stack1 : stacks) inventory.setStack(Arrays.stream(stacks).toList().indexOf(stack1), stack1);
                    inventory.setStack(18, stack);

                    openSimpleMenu(user, user, inventory, Text.translatable("item.dabaosword.qice.screen"));
                }
                else {user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);}
            } else {user.sendMessage(Text.translatable("item.dabaosword.qice.tip").formatted(Formatting.RED), true);}
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slot) {
            if (selected.isEmpty()) return;
            if (!player.isCreative()) {
                while (countCards(player) > 0) {cardDecrement(getCard(player, isCard), 64);}
                setCD(stack, 20);
            }
            give(player, selected);
            voice(player, Sounds.QICE);
            closeGUI(player);
        }
    }

    public static class Qingguo extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qingguo.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 5, isBlackCard, new ItemStack(ModItems.SHAN));
            super.tick(stack, slot, entity);
        }
    }

    public static class Quanji extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int quan = getTag(stack);
            tooltip.add(Text.of("权："+quan));
            tooltip.add(Text.translatable("item.dabaosword.quanji.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.quanji.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient && !user.isSneaking()) voice(user, Sounds.ZILI);
            return super.use(world, user, hand);
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity) {
                int quan = getTag(stack);
                setTag(stack, quan + 1);
                voice(entity, Sounds.QUANJI);
            }
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity entity, DamageSource source, float amount) {
            if (source.getSource() instanceof LivingEntity s && hasTrinket(SkillCards.QUANJI, s)) {
                ItemStack stack = trinketItem(SkillCards.QUANJI, s);
                int quan = getTag(stack);
                if (quan > 0) {
                    if (quan > 4) draw(entity, 2);
                    setTag(stack, quan/2);
                    voice(s, Sounds.PAIYI);
                    return new Pair<>(0f, (float) quan);
                }
            }
            return null;
        }
    }

    public static class Shanzhuan extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(Text.translatable("item.dabaosword.shanzhuan.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.shanzhuan.tooltip2").formatted(Formatting.BLUE));
        }

        //擅专：我言既出，谁敢不从！
        @Override
        public void postDamage(ItemStack stack, LivingEntity entity, LivingEntity attacker, float amount) {
            if (attacker instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                if (entity instanceof PlayerEntity target) {
                    if (countAllCards(target) > 0) openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, false, true, false, 1);
                } else {
                    voice(player, Sounds.SHANZHUAN);
                    if (new Random().nextFloat() < 0.5) {
                        entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                    } else {entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));}
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                }
            }
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slotIndex) {
            voice(player, Sounds.SHANZHUAN);
            if (isRedCard.test(selected)) target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
            else target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1,1));
            Text message = Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText());
            player.sendMessage(message);
            target.sendMessage(message);
            cardDiscard(target, selected, 1, slotIndex < 4);
            player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 12,0,false,false,true));
            closeGUI(player);
        }
    }

    public static class Shensu extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.shensu.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.shensu.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getSource() instanceof LivingEntity attacker) {
                if (hasTrinket(SkillCards.SHENSU, attacker) && !attacker.hasStatusEffect(ModItems.COOLDOWN)) {
                    float walkSpeed = 4.317f;
                    float speed = Objects.requireNonNull(trinketItem(SkillCards.SHENSU, attacker).getNbt()).getFloat("speed");
                    if (speed > walkSpeed) {
                        float m = (speed - walkSpeed) / walkSpeed / 2;
                        attacker.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, (int) (5 * 20 * m),0,false,false,true));
                        if (attacker instanceof PlayerEntity player) player.sendMessage(Text.translatable("shensu.info", speed, m));
                        voice(attacker, Sounds.SHENSU);
                        return new Pair<>(m, 0f);
                    }
                }
            }
            return null;
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(player)) {
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

    public static class Yiji extends SkillItem.ActiveSkillWithTarget {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 20s"));
            tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip2").formatted(Formatting.BLUE));
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (entity instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
                draw(player, 2);
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                setTag(stack, 2);
                voice(player, Sounds.YIJI);
            }
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            int i = getTag(stack);
            if (i > 0 ) openInv(user, target, Text.translatable("give_card.title", stack.getName()), stack, true, false, false, 2);
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slotIndex) {
            int i = getTag(stack);
            Text message = Text.translatable("give_card.tip", player.getDisplayName(), stack.toHoverableText(), target.getDisplayName(), selected.toHoverableText());
            target.sendMessage(message);
            player.sendMessage(message);
            cardMove(player, target, selected, 1, false, false);
            setTag(stack, i - 1);
            if (i - 1 == 0) closeGUI(player);
        }
    }

    public static class Xingshang extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.xingshang.tooltip").formatted(Formatting.BLUE));
        }
    }
}
