package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.event.ActiveSkillHandler;
import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.GiftBoxItem;
import com.amotassic.dabaosword.util.*;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SkillItem extends TrinketItem implements Skill {
    public SkillItem(Settings settings) {super(settings);}

    public static class Benxi extends SkillItem {
        public Benxi(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int benxi = getTag(stack);
            tooltip.add(Text.of("奔袭：" + benxi));
            tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noLongHand(player) && noTieji(entity)) {
                int benxi = getTag(stack);
                if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 2,false,false,true));
                } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 1,false,false,true));
                } else if (benxi != 0) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi - 1,false,false,true));
                }
            }
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {
            if (attacker instanceof PlayerEntity player && !player.getCommandTags().contains("benxi")) {
                int ben = getTag(stack);
                if (ben > 1) {
                    player.addCommandTag("benxi");
                    setTag(stack, ben - 2);
                    draw(player);
                    voice(player, Sounds.BENXI);
                }
            }
        }

        private boolean noLongHand(PlayerEntity player) {
            return player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
        }
    }

    public static class Duanliang extends SkillItem {
        public Duanliang(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.duanliang.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 5, ModTools::nonBasic, new ItemStack(ModItems.BINGLIANG_ITEM), Sounds.DUANLIANG);
            super.tick(stack, slot, entity);
        }
    }

    public static class Fangzhu extends SkillItem {
        public Fangzhu(Settings settings) {super(settings);}

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
        public Ganglie(Settings settings) {super(settings);}

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
                        if (attacker instanceof PlayerEntity target) {//如果来源是玩家则弃牌
                            List<ItemStack> candidate = new ArrayList<>();
                            //把背包中的卡牌添加到待选物品中
                            DefaultedList<ItemStack> inventory = target.getInventory().main;
                            List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(j -> isCard(inventory.get(j))).boxed().toList();
                            for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
                            //把饰品栏的卡牌添加到待选物品中
                            int equip = 0; //用于标记装备区牌的数量
                            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
                            if(component.isPresent()) {
                                List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                                for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                                    ItemStack stack1 = entry.getRight();
                                    if(stack1.isIn(Tags.Items.CARD)) candidate.add(stack1); equip++;
                                }
                            }
                            if(!candidate.isEmpty()) {
                                int index = new Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                target.sendMessage(Text.literal(entity.getEntityName()).append(Text.translatable("dabaosword.discard")).append(chosen.toHoverableText()));
                                CardDiscardCallback.EVENT.invoker().cardDiscard(target, chosen, 1, index > candidate.size() - equip);
                            }
                        } else {//如果来源不是玩家则随机弃置它的主副手物品和装备
                            List<ItemStack> candidate = new ArrayList<>();
                            if (!attacker.getMainHandStack().isEmpty()) candidate.add(attacker.getMainHandStack());
                            if (!attacker.getOffHandStack().isEmpty()) candidate.add(attacker.getOffHandStack());
                            for (ItemStack armor : attacker.getArmorItems()) {if (!armor.isEmpty()) candidate.add(armor);}
                            if(!candidate.isEmpty()) {
                                int index = new Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                chosen.decrement(1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Gongao extends SkillItem {
        public Gongao(Settings settings) {super(settings);}

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

    public static class Guose extends SkillItem {
        public Guose(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.guose.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 15, s -> s.isOf(ModItems.SHAN), new ItemStack(ModItems.TOO_HAPPY_ITEM), Sounds.GUOSE);
            super.tick(stack, slot, entity);
        }
    }

    public static class Huoji extends SkillItem {
        public Huoji(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.huoji.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 15, s -> s.isIn(Tags.Items.BASIC_CARD), new ItemStack(ModItems.FIRE_ATTACK), Sounds.HUOJI);
            super.tick(stack, slot, entity);
        }
    }

    public static class Kanpo extends SkillItem {
        public Kanpo(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 10s" : "CD: 10s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.kanpo.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 10, s -> s.isIn(Tags.Items.ARMOURY_CARD), new ItemStack(ModItems.WUXIE), Sounds.KANPO);
            super.tick(stack, slot, entity);
        }
    }

    public static class Kuanggu extends SkillItem {
        public Kuanggu(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(Text.translatable("item.dabaosword.kuanggu.tooltip").formatted(Formatting.RED));
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity target, LivingEntity attacker, float amount) {
            if (attacker instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                if (player.getMaxHealth() - player.getHealth()>=5) player.heal(5);
                else draw(player);
                voice(player, Sounds.KUANGGU);
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
            }
        }
    }

    public static class Lianying extends SkillItem {
        public Lianying(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.lianying.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player) {
                int cd = getCD(stack);
                if (world.getTime() % 20 == 0 && cd == 1) { //确保一秒内只触发一次
                    draw(player);
                    voice(player, Sounds.LIANYING);
                }
            }
            super.tick(stack, slot, entity);
        }
    }

    public static class Liegong extends SkillItem {
        public Liegong(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                //烈弓：命中后给目标一个短暂的冷却效果，防止其自动触发闪
                target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient && noTieji(entity)) {
                if (!entity.hasStatusEffect(ModItems.COOLDOWN)) gainReach(entity,13);
                else gainReach(entity,0);
            }
            super.tick(stack, slot, entity);
        }

        @Override
        public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (!entity.getWorld().isClient) gainReach(entity,0);
        }

        private void gainReach(LivingEntity entity, int value) {
            EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("2b3df518-6e44-3554-821b-232333bcef5c"), "Range 13", value, EntityAttributeModifier.Operation.ADDITION);
            Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.REACH, AttributeModifier, ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

            entity.getAttributes().addTemporaryModifiers(rangeModifier.get());
        }
    }

    public static class Longdan extends SkillItem {
        public Longdan(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.longdan.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.longdan.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player && noTieji(entity)) {
                ItemStack stack1 = player.getOffHandStack();
                if (world.getTime() % 20 == 0 && stack1.isIn(Tags.Items.BASIC_CARD)) {
                    stack1.decrement(1);
                    if (stack1.isIn(Tags.Items.SHA)) give(player, new ItemStack(ModItems.SHAN));
                    if (stack1.getItem() == ModItems.SHAN) give(player, new ItemStack(ModItems.SHA));
                    if (stack1.getItem() == ModItems.PEACH) give(player, new ItemStack(ModItems.JIU));
                    if (stack1.getItem() == ModItems.JIU) give(player, new ItemStack(ModItems.PEACH));
                    voice(player, Sounds.LONGDAN);
                }
            }
            super.tick(stack, slot, entity);
        }
    }

    public static class Luanji extends SkillItem {
        public Luanji(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luanji.tooltip"));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 15, s -> s.isIn(Tags.Items.CARD) && s.getCount() > 1, 2, new ItemStack(ModItems.WANJIAN), Sounds.LUANJI);
            super.tick(stack, slot, entity);
        }
    }

    public static class Luoyi extends SkillItem {
        public Luoyi(Settings settings) {super(settings);}

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

    public static class Pojun extends SkillItem {
        public Pojun(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(Text.translatable("item.dabaosword.pojun.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //破军：攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                ItemStack head = target.getEquippedStack(EquipmentSlot.HEAD);
                ItemStack chest = target.getEquippedStack(EquipmentSlot.CHEST);
                ItemStack legs = target.getEquippedStack(EquipmentSlot.LEGS);
                ItemStack feet = target.getEquippedStack(EquipmentSlot.FEET);
                if (target instanceof PlayerEntity player1) {
                    if (!head.isEmpty()) {give(player1, head.copy()); head.setCount(0);}
                    if (!chest.isEmpty()) {give(player1, chest.copy()); chest.setCount(0);}
                    if (!legs.isEmpty()) {give(player1, legs.copy()); legs.setCount(0);}
                    if (!feet.isEmpty()) {give(player1, feet.copy()); feet.setCount(0);}
                } else {
                    if (!head.isEmpty()) {target.dropStack(head.copy());head.setCount(0);}
                    if (!chest.isEmpty()) {target.dropStack(chest.copy());chest.setCount(0);}
                    if (!legs.isEmpty()) {target.dropStack(legs.copy());legs.setCount(0);}
                    if (!feet.isEmpty()) {target.dropStack(feet.copy());feet.setCount(0);}
                }
                voice(player, Sounds.POJUN);
                int i = target instanceof PlayerEntity ? 200 : 40;
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, i,0, false,false,true));
            }
        }
    }

    public static class Qingguo extends SkillItem {
        public Qingguo(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qingguo.tooltip").formatted(Formatting.BLUE));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 5, ModTools::nonBasic, new ItemStack(ModItems.SHAN), Sounds.QINGGUO);
            super.tick(stack, slot, entity);
        }
    }

    public static class Qixi extends SkillItem {
        public Qixi(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qixi.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity instanceof PlayerEntity player) viewAs(player, stack, 5, ModTools::nonBasic, new ItemStack(ModItems.DISCARD), Sounds.QIXI);
            super.tick(stack, slot, entity);
        }
    }

    public static class Quanji extends SkillItem {
        public Quanji(Settings settings) {super(settings);}

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
    }

    public static class Shanzhuan extends SkillItem {
        public Shanzhuan(Settings settings) {super(settings);}

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
                if (entity instanceof PlayerEntity target) ActiveSkillHandler.openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, ActiveSkillHandler.targetInv(target, true, false, 1));
                else {
                    voice(player, Sounds.SHANZHUAN);
                    if (new Random().nextFloat() < 0.5) {
                        entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                    } else {
                        entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                    }
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                }
            }
        }
    }

    public static class Shensu extends SkillItem {
        public Shensu(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.shensu.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.shensu.tooltip2").formatted(Formatting.BLUE));
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

    public static class Tieji extends SkillItem {
        public Tieji(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip2").formatted(Formatting.RED));
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            if (getShaSlot(player) != -1) {
                voice(player, Sounds.TIEJI);
                target.addStatusEffect(new StatusEffectInstance(ModItems.TIEJI,200,0,false,true,true));
                if (new Random().nextFloat() < 0.75) target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
            }
        }
    }

    public static class Yiji extends ActiveSkillWithTarget {
        public Yiji(Settings settings) {super(settings);}

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
    }

    public static class Zhiheng extends SkillItem {
        public Zhiheng(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int z = getTag(stack);
            tooltip.add(Text.literal("可用次数：" + z));
            tooltip.add(Text.translatable("item.dabaosword.zhiheng.tooltip1").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.zhiheng.tooltip2").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world) {
                int z = getTag(stack);
                if (z < 10) {
                    if (world.getTime() % 100 == 0) z++; setTag(stack, z);
                }
            }
            super.tick(stack, slot, entity);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        if (stack.getItem() == SkillCards.XIAOJI) {
            tooltip.add(Text.translatable("item.dabaosword.xiaoji.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.ZHIJIAN) {
            tooltip.add(Text.translatable("item.dabaosword.zhijian.tooltip1").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.zhijian.tooltip2").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.GONGXIN) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.gongxin.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.RENDE) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.rende.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.rende.tooltip2").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.BUQU) {
            int c = getTag(stack);
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip1").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip2").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip3").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip4").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip5").formatted(Formatting.GREEN));
            } else {
                tooltip.add(Text.literal("创：" + c));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("dabaosword.shifttooltip"));
            }
        }

        if (stack.getItem() == SkillCards.XINGSHANG) {
            tooltip.add(Text.translatable("item.dabaosword.xingshang.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LUOSHEN) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luoshen.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.QICE) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.qice.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LEIJI) {
            tooltip.add(Text.translatable("item.dabaosword.leiji.tooltip"));
        }

        if (stack.getItem() == SkillCards.JIZHI) {
            tooltip.add(Text.translatable("item.dabaosword.jizhi.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.KUROU) {
            tooltip.add(Text.translatable("item.dabaosword.kurou.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.TAOLUAN) {
            tooltip.add(Text.translatable("item.dabaosword.taoluan.tooltip"));
        }

        if (stack.getItem() == SkillCards.JUEQING) {
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LIULI) {
            tooltip.add(Text.translatable("item.dabaosword.liuli.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.MASHU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == SkillCards.FEIYING) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && !equipped(stack)) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.literal(entity.getEntityName()).append(Text.literal("装备了 ").append(stack.toHoverableText()))
            ));
            setEquipped(stack, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && equipped(stack)) setEquipped(stack, false);
    }

    private boolean equipped(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().contains("equipped");
        }
        return false;
    }

    private void setEquipped(ItemStack stack, boolean equipped) {
        if (equipped) {
            NbtCompound nbt = stack.getOrCreateNbt(); nbt.putBoolean("equipped", true);
            stack.setNbt(nbt);
        } else if (stack.getNbt() != null) stack.getNbt().remove("equipped");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient && user.getCommandTags().contains("change_skill") && hand == Hand.OFF_HAND && user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.isIn(Tags.Items.SKILL)) {
                stack.setCount(0);
                changeSkill(user);
                user.getCommandTags().remove("change_skill");
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (stack.getNbt() != null) {
                if (stack.getNbt().contains("cooldown")) {
                    int cd = stack.getNbt().getInt("cooldown");
                    if (world.getTime() % 20 == 0) { //世界时间除以20取余为0时，技能内置CD减一秒
                        if (cd > 0) cd--; setCD(stack, cd);
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    public static void changeSkill(PlayerEntity player) {
        List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/change_skill.json"));
        LootEntry selectedEntry = GiftBoxItem.selectRandomEntry(lootEntries);

        ItemStack stack = new ItemStack(Registries.ITEM.get(selectedEntry.item()));
        if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
        give(player, stack);
    }

    public static class ActiveSkill extends SkillItem {
        public ActiveSkill(Settings settings) {super(settings);}
    }

    public static class ActiveSkillWithTarget extends SkillItem {
        public ActiveSkillWithTarget(Settings settings) {super(settings);}
    }
}
