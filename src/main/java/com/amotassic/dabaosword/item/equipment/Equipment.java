package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.amotassic.dabaosword.item.skillcard.SkillItem.equipped;
import static com.amotassic.dabaosword.item.skillcard.SkillItem.setEquipped;
import static com.amotassic.dabaosword.util.ModTools.*;
import static com.amotassic.dabaosword.util.ModifyDamage.shan;

public class Equipment extends TrinketItem implements Card, Skill {
    public Equipment() {super(new Settings().maxCount(1));}

    public static class BaguaArmor extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.bagua.tooltip").formatted(Formatting.AQUA));
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.HIGH;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            if (source.getAttacker() instanceof LivingEntity) {
                if (!target.hasStatusEffect(ModItems.COOLDOWN2)) {
                    if (hasTrinket(ModItems.BAGUA, target) && new Random().nextFloat() < 0.5 && !source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
                        shan(target, true, source, amount);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class BaiyinArmor extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.baiyin.tooltip").formatted(Formatting.AQUA));
        }

        @Override
        public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
            if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.getAttacker() instanceof LivingEntity && hasTrinket(ModItems.BAIYIN, target)) {
                voice(target, Sounds.BAIYIN);
                return new Pair<>(-0.4f, 0f);
            }
            return null;
        }
    }

    public static class FangtianWeapon extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip2").formatted(Formatting.AQUA));
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
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(Formatting.AQUA));
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
            return null;
        }
    }

    public static class HanbingWeapon extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity entity, LivingEntity attacker, float amount) {
            voice(attacker, Sounds.HANBING);
            entity.timeUntilRegen = 0;
            entity.setFrozenTicks(500);
        }
    }

    public static class QinggangWeapon extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
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
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip2").formatted(Formatting.AQUA));
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

    public static class RenwangArmor extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.renwang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.renwang.tooltip2").formatted(Formatting.AQUA));
        }
    }

    public static class RattanArmor extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.rattanarmor.tooltip"));
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
            return null;
        }

        @Override
        public Priority getPriority(LivingEntity target, DamageSource source, float amount) {return Priority.HIGH;}

        @Override
        public boolean cancelDamage(LivingEntity target, DamageSource source, float amount) {
            ItemStack stack = trinketItem(ModItems.RATTAN_ARMOR, target);
            //弹射物对藤甲无效
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(target)) {
                Entity projectile = source.getSource();
                if (projectile instanceof ArrowEntity) { //即使处于CD中，箭也对藤甲无效
                    projectile.discard();
                    voice(target, Sounds.TENGJIA1);
                    return true;
                }
                if (getCD(stack) == 0) {
                    if (projectile != null) projectile.discard();
                    setCD(stack, 5);
                    target.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    voice(target, Sounds.TENGJIA1);
                    return true;
                }
            }
            //若攻击者主手没有物品，则无法击穿藤甲
            if (source.getSource() instanceof LivingEntity s && inrattan(target) && s.getMainHandStack().isEmpty()) {
                if (getCD(stack) == 0) {
                    setCD(stack, 5);
                    target.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    voice(target, Sounds.TENGJIA1);
                    return true;
                }
            }
            return false;
        }

        private static boolean inrattan(LivingEntity entity) {return hasTrinket(ModItems.RATTAN_ARMOR, entity);}
    }

    public static class ZhangbaWeapon extends Equipment {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(Text.translatable("item.dabaosword.zhangba.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.zhangba.tooltip2").formatted(Formatting.AQUA));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            super.tick(stack, slot, entity);
            if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && getCD(stack) == 0) {
                ItemStack off = player.getOffHandStack();
                NbtCompound nbt = getOrCreateNbt(stack);
                boolean one = nbt.contains("has_one");
                if (isCard(off)) {
                    if (one) {
                        nbt.remove("has_one");
                        setCD(stack, 5);
                        give(player, new ItemStack(ModItems.SHA));
                        voice(player, Sounds.ZHANGBA);
                    } else {nbt.putBoolean("has_one", true);}
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                    off.decrement(1);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var sr = getSuitAndRank(stack);
        if (sr != null) {
            Card.Suits suit = sr.getLeft(); Card.Ranks rank = sr.getRight();
            if (isRedCard.test(stack)) tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank).formatted(Formatting.RED));
            else tooltip.add(Text.translatable("card.suit_and_rank", suit.suit, rank.rank));
        }

        if (stack.getItem() == ModItems.CHITU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == ModItems.DILU) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("equipment.tip1").formatted(Formatting.BOLD));
            tooltip.add(Text.translatable("equipment.tip2").formatted(Formatting.BOLD));
        } else tooltip.add(Text.translatable("dabaosword.shifttooltip"));

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
                    Text.translatable("dabaosword.entity.equip", entity.getDisplayName(), stack.toHoverableText())
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
        if (entity instanceof PlayerEntity player && !player.isCreative()) return false;
        return super.canUnequip(stack, slot, entity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(player, player.getMainHandStack(), player)) return TypedActionResult.success(player.getMainHandStack());
        }
        return super.use(world, player, hand);
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
        useOrReplaceEquip(user, stack);
    }

    public static void useOrReplaceEquip(LivingEntity user, ItemStack stack) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(user);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            SlotReference firstSlot = null;

            for (Map<String, TrinketInventory> group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack s = inv.getStack(i);
                        SlotReference ref = new SlotReference(inv, i);
                        if (TrinketSlot.canInsert(stack, ref, user)) {
                            if (s.isEmpty()) { //如果这个槽位没有物品，则直接放入
                                inv.setStack(i, stack.copy());
                                return;
                            } else if (firstSlot == null) firstSlot = ref;
                            //记录第一个有物品的槽位（也就是说，只能替换同类槽位的第一个物品）
                        }
                    }
                }
            }

            if (firstSlot != null) { //替换原有装备
                ItemStack preStack = firstSlot.inventory().getStack(firstSlot.index());
                cardDiscard(user, preStack, preStack.getCount(), true);
                firstSlot.inventory().setStack(firstSlot.index(), stack.copy());
            }
        }
    }

    /*public static boolean replaceEquip(PlayerEntity player, ItemStack stack) {
        var slots = replaceSlot(player, stack);
        if (!slots.isEmpty()) {
            SlotReference ref = slots.get(new Random().nextInt(slots.size()));
            ItemStack preStack = ref.inventory().getStack(ref.index());
            cardDiscard(player, preStack, preStack.getCount(), true);
            ref.inventory().setStack(ref.index(), stack.copy());
            cardUsePost(player, stack, player);
            return true;
        }
        return false;
    }
    //旧版的随机替换同类槽位装备的逻辑，以防万一暂且保留
    private static List<SlotReference> replaceSlot(PlayerEntity player, ItemStack stack) {
        List<SlotReference> slots = new ArrayList<>();
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        //如果对应装备栏的物品与待装备的物品有完全相同的标签，则记录该槽位
                        if (!inv.getStack(i).isEmpty() && inv.getStack(i).streamTags().toList().equals(stack.streamTags().toList())) {
                            slots.add(new SlotReference(inv, i));
                        }
                    }
                }
            }
        }
        return slots;
    }*/
}
