package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Skill;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.*;

import static com.amotassic.dabaosword.item.skillcard.SkillItem.equipped;
import static com.amotassic.dabaosword.item.skillcard.SkillItem.setEquipped;
import static com.amotassic.dabaosword.util.ModTools.*;
import static com.amotassic.dabaosword.util.ModifyDamage.shan;

public class Equipment extends TrinketItem implements Skill {
    public Equipment(Settings settings) {super(settings);}

    public static class BaguaArmor extends Equipment {
        public BaguaArmor(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.bagua.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.HIGH;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity) {
                if (!target.hasStatusEffect(ModItems.COOLDOWN2) && !target.getCommandTags().contains("juedou")) {
                    if (hasTrinket(ModItems.BAGUA, target) && new Random().nextFloat() < 0.5 && !source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
                        shan(target, true, source);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class BaiyinArmor extends Equipment {
        public BaiyinArmor(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.baiyin.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.getAttacker() instanceof LivingEntity && hasTrinket(ModItems.BAIYIN, target)) {
                voice(target, Sounds.BAIYIN);
                return new Pair<>(-0.4f, 0f);
            }
            return super.modifyDamage(target, source, amount);
        }
    }

    public static class FangtianWeapon extends Equipment {
        public FangtianWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //方天画戟：打中生物后触发特效，给予CD和持续时间
            int cd = getCD(stack);
            if (cd == 0) {
                setCD(stack, 20);
                voice(player, Sounds.FANGTIAN);
                player.sendMessage(Text.translatable("dabaosword.fangtian").formatted(Formatting.RED), true);
            }
        }
    }

    public static class GudingWeapon extends Equipment {
        public GudingWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getSource() instanceof LivingEntity attacker && hasTrinket(ModItems.GUDING_WEAPON, attacker)) {
                int i = 0;
                for (var s : target.getArmorItems()) {if (s.isEmpty()) i++;}
                if (i == 4) {
                    voice(attacker, Sounds.GUDING);
                    return new Pair<>(0f, 5f);
                }
            }
            return super.modifyDamage(target, source, amount);
        }
    }

    public static class HanbingWeapon extends Equipment {
        public HanbingWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity entity, LivingEntity attacker, float amount) {
            voice(attacker, Sounds.HANBING);
            entity.timeUntilRegen = 0;
            entity.setFrozenTicks(500);
        }
    }

    public static class QinggangWeapon extends Equipment {
        public QinggangWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //青釭剑额外伤害
            float extraDamage = Math.min(20, 0.2f * target.getMaxHealth());
            target.damage(player.getDamageSources().genericKill(), extraDamage); target.timeUntilRegen = 0;
            voice(player, Sounds.QINGGANG);
        }
    }

    public static class QinglongWeapon extends Equipment {
        public QinglongWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, context, tooltip, type);
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            voice(player, Sounds.QINGLONG);
            player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,10,0,false,false,false));
            player.teleport(target.getX(), target.getY(), target.getZ(), false);
            Vec3d momentum = player.getRotationVector().multiply(2);
            target.velocityModified = true; target.setVelocity(momentum.getX(),0 ,momentum.getZ());
        }
    }

    public static class RattanArmor extends Equipment {
        public RattanArmor(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("item.dabaosword.rattanarmor.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }

        //实现渡江不沉的效果，代码来自https://github.com/focamacho/RingsOfAscension/中的水上行走戒指
        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            super.tick(stack, slot, entity);
            if(entity.isSneaking()) return;
            BlockPos pos = entity.getBlockPos();
            boolean water = entity.getWorld().getFluidState(new BlockPos(pos.getX(),
                    (int) (entity.getBoundingBox().getMin(Direction.Axis.Y)), pos.getZ())).isOf(Fluids.WATER);
            if(water) {
                Vec3d motion = entity.getVelocity();
                entity.setVelocity(motion.x, 0.0D, motion.z);
                entity.fallDistance = 0;
                entity.setOnGround(true);
            }
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！（伤害大于5就只加5）
            if (source.isIn(DamageTypeTags.IS_FIRE) && hasTrinket(ModItems.RATTAN_ARMOR, target)) {
                voice(target, Sounds.TENGJIA2);
                return new Pair<>(0f, Math.min(amount, 5f));
            }
            return super.modifyDamage(target, source, amount);
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.HIGH;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            //弹射物对藤甲无效
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(target)) {
                voice(target, Sounds.TENGJIA1);
                if (source.getSource() != null) source.getSource().discard();
                return true;
            }
            //若攻击者主手没有物品，则无法击穿藤甲
            if (source.getSource() instanceof LivingEntity s && inrattan(target) && s.getMainHandStack().isEmpty()) {
                ItemStack stack = trinketItem(ModItems.RATTAN_ARMOR, target);
                if (getCD(stack) == 0) {
                    setCD(stack, 3);
                    voice(target, Sounds.TENGJIA1);
                    return true;
                }
            }
            return false;
        }

        private static boolean inrattan(LivingEntity entity) {return hasTrinket(ModItems.RATTAN_ARMOR, entity);}
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {

        if (stack.getItem() == ModItems.CHITU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == ModItems.DILU) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        if (stack.getItem() == ModItems.CARD_PILE) {
            tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        }

        if (stack.getItem() != ModItems.CARD_PILE) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("equipment.tip1").formatted(Formatting.BOLD));
                tooltip.add(Text.translatable("equipment.tip2").formatted(Formatting.BOLD));
            } else tooltip.add(Text.translatable("dabaosword.shifttooltip"));
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int cd = getCD(stack); //世界时间除以20取余为0时，技能内置CD减一秒
            if (cd > 0 && world.getTime() % 20 == 0) setCD(stack, cd - 1);
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && !equipped(stack)) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.literal(entity.getNameForScoreboard()).append(Text.literal("装备了 ").append(stack.toHoverableText()))
            ));
            setEquipped(stack, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && equipped(stack)) setEquipped(stack, false);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player && player.isCreative()) return true;
        if (stack.getItem() != ModItems.CARD_PILE) return false;
        return super.canUnequip(stack, slot, entity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (useEquip(user, stack)) return TypedActionResult.success(stack, world.isClient);
        if (replaceEquip(user, stack)) return TypedActionResult.success(stack, world.isClient);
        return TypedActionResult.pass(stack);
    }

    public static boolean useEquip(PlayerEntity user, ItemStack stack) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(user);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (Map<String, TrinketInventory> group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (inv.getStack(i).isEmpty()) {
                            SlotReference ref = new SlotReference(inv, i);
                            if (TrinketSlot.canInsert(stack, ref, user)) {
                                ItemStack newStack = stack.copy();
                                inv.setStack(i, newStack);
                                Trinket trinket = TrinketsApi.getTrinket(stack.getItem());
                                RegistryEntry<SoundEvent> soundEvent = trinket.getEquipSound(stack, ref, user);
                                if (!stack.isEmpty() && soundEvent != null) {
                                    user.emitGameEvent(GameEvent.EQUIP);
                                    user.playSound(soundEvent.value(), 1.0F, 1.0F);
                                }
                                cardUsePost(user, stack, user);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean replaceEquip(PlayerEntity player, ItemStack stack) {
        Map<Integer, TrinketInventory> map = replaceSlot(player, stack);
        if (!map.isEmpty() && stack.getItem() != ModItems.CARD_PILE) {
            List<Integer> slots = map.keySet().stream().toList();
            int index = new Random().nextInt(slots.size()); int i = slots.get(index);
            ItemStack preStack = map.values().stream().toList().get(index).getStack(i);
            cardDiscard(player, preStack, preStack.getCount(), true);
            map.values().stream().toList().get(index).setStack(i, stack.copy());
            cardUsePost(player, stack, player);
            return true;
        }
        return false;
    }

    private static Map<Integer, TrinketInventory> replaceSlot(PlayerEntity player, ItemStack stack) {
        Map<Integer, TrinketInventory> m = new HashMap<>();
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        //如果对应装备栏的物品与待装备的物品有完全相同的标签，则将该饰品栏添加到map中
                        if (!inv.getStack(i).isEmpty() && inv.getStack(i).streamTags().toList().equals(stack.streamTags().toList())) {
                            m.put(i, inv);
                        }
                    }
                }
            }
        }
        return m;
    }
}
