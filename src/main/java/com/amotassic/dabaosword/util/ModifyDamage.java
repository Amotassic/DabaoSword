package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ModifyDamage {
    //由于fabric没有提供修改伤害的方法，因此我自己注入"applyArmorToDamage"以修改生物受到的伤害值
    public static float modify(LivingEntity entity, DamageSource source, float value) {
        float multiply = 0; //倍率增伤乘区
        float add = 0; //固定数值加减伤害区
        List<Float> reducing = new ArrayList<>(); //最终减伤乘区，存储负值，每个值使最终伤害为原来的（1+x）倍

        //处理所有饰品带来的增/减伤结算
        List<Pair<Pair<Float, Float>, List<Float>>> pairList = new ArrayList<>();
        pairList.add(calculateDMG(entity, source, value, entity));
        if (source.getSource() instanceof LivingEntity SE) {
            if (!SE.getCommandTags().contains("sha")) { //防止杀的效果再次触发近战加伤
                pairList.add(calculateDMG(entity, source, value, SE));

                //插入一个武器版古锭刀的结算
                int i = 0; //i == 4则说明受击者的盔甲栏没有任何物品
                for (var s : entity.getArmorItems()) {if (s.isEmpty()) i++;}
                if (i == 4 && SE.getMainHandStack().isOf(ModItems.GUDINGDAO)) multiply += 1;
            }
        } else if (source.getAttacker() instanceof LivingEntity AT) pairList.add(calculateDMG(entity, source, value, AT));
        for (var p : pairList) {
            multiply += p.getLeft().getLeft();
            add += p.getLeft().getRight();
            reducing.addAll(p.getRight());
        }

        //伤害结算
        value = value * (1 + multiply) + add;
        for (var f : reducing) {value *= (1 + f);}

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            entity.damageArmor(source, value);
            value = DamageUtil.getDamageLeft(value, entity.getArmor(), (float)entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return value;
    }

    private static Pair<Pair<Float, Float>, List<Float>> calculateDMG(LivingEntity entity, DamageSource source, float value, LivingEntity trinketOwner) {
        float m = 0; float a = 0;
        List<Float> r = new ArrayList<>();
        for (var p : allTinkets(trinketOwner)) {
            var stack = p.getRight();
            if (stack.getItem() instanceof SkillItem skill) {
                var pair = skill.modifyDamage(entity, source, value);
                if (pair.getLeft() < 0) r.add(pair.getLeft()); else m += pair.getLeft();
                a += pair.getRight();
            }
            if (stack.getItem() instanceof Equipment skill) {
                var pair = skill.modifyDamage(entity, source, value);
                if (pair.getLeft() < 0) r.add(pair.getLeft()); else m += pair.getLeft();
                a += pair.getRight();
            }
        }
        return new Pair<>(new Pair<>(m, a), r);
    }

    public static boolean shouldCancel(LivingEntity entity, DamageSource source, float amount) {
        //无敌效果
        if (entity.hasStatusEffect(ModItems.INVULNERABLE) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        //弹射物对藤甲无效
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(entity)) {
            voice(entity, Sounds.TENGJIA1);
            if (source.getSource() != null) source.getSource().discard();
            return true;
        }

        /*List<List<ItemStack>> list = new ArrayList<>();
        for (var l : list) {
            for (var s : l) {
                if (s.getItem() instanceof SkillItem) {

                }
            }
        }*/

        if (source.getSource() instanceof LivingEntity sourceEntity) {
            //沈佳宜防御效果
            if (!(sourceEntity instanceof PlayerEntity) && entity.hasStatusEffect(ModItems.DEFEND)) {
                if (Objects.requireNonNull(entity.getStatusEffect(ModItems.DEFEND)).getAmplifier() >= 2) return true;
            }

            //若攻击者主手没有物品，则无法击穿藤甲
            if (inrattan(entity) && sourceEntity.getMainHandStack().isEmpty()) {
                voice(entity, Sounds.TENGJIA1);
                return true;
            }

            //决斗等物品虽然手长，但过远时普通伤害无效
            if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && entity.distanceTo(sourceEntity) > 5) {
                if (sourceEntity.getMainHandStack().getItem() == ModItems.JUEDOU || sourceEntity.getMainHandStack().getItem() == ModItems.DISCARD) return true;
            }

            //被乐的生物无法造成普通攻击伤害
            if (sourceEntity.hasStatusEffect(ModItems.TOO_HAPPY)) return true;
        }

        if (source.getSource() instanceof WolfEntity dog && dog.hasStatusEffect(ModItems.INVULNERABLE)) {
            //被南蛮入侵的狗打中可以消耗杀以免疫伤害
            if (entity instanceof PlayerEntity player) {
                if (getShaSlot(player) != -1) {
                    ItemStack stack = player.getMainHandStack().isIn(Tags.Items.SHA) ? player.getMainHandStack() : shaStack(player);
                    if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                    if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                    if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                    cardUsePost(player, stack, null);
                    dog.setHealth(0);
                    return true;
                }
                dog.setHealth(0);
            }
        }

        if (source.getAttacker() instanceof LivingEntity attacker) {

            if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                //被乐的生物的弹射物无法造成伤害
                if (attacker.hasStatusEffect(ModItems.TOO_HAPPY)) return true;
            }

            if (entity instanceof PlayerEntity player) {
                //流离
                if (hasTrinket(SkillCards.LIULI, player) && hasItemInTag(Tags.Items.CARD, player) && !player.hasStatusEffect(ModItems.INVULNERABLE) && !player.isCreative()) {
                    ItemStack stack = stackInTag(Tags.Items.CARD, player);
                    LivingEntity nearEntity = getLiuliEntity(player, attacker);
                    if (nearEntity != null) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 10,0,false,false,false));
                        voice(player, Sounds.LIULI);
                        cardDiscard(player, stack, 1, false);
                        nearEntity.timeUntilRegen = 0; nearEntity.damage(source, amount);
                        return true;
                    }
                }
            }

            //闪的被动效果
            final boolean trigger = baguaTrigger(entity);
            boolean hasShan = entity instanceof PlayerEntity player ?
                    getShanSlot(player) != -1 || trigger : entity.getOffHandStack().getItem() == ModItems.SHAN || trigger;
            boolean common = !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && hasShan && !entity.hasStatusEffect(ModItems.COOLDOWN2) && !entity.hasStatusEffect(ModItems.INVULNERABLE);
            boolean shouldShan = entity instanceof PlayerEntity player ?
                    common && !entity.getCommandTags().contains("juedou") && !player.isCreative() : common;
            if (shouldShan) {
                shan(entity, trigger);
                //虽然没有因为杀而触发闪，但如果攻击者的杀处于自动触发状态，则仍会消耗
                if (source.getSource() instanceof PlayerEntity player && getShaSlot(player) != -1) {
                    ItemStack stack = player.getMainHandStack().isIn(Tags.Items.SHA) ? player.getMainHandStack() : shaStack(player);
                    if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                    if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                    if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                    cardUsePost(player, stack, entity);
                }
                return true;
            }

            if (hasTrinket(SkillCards.JUEQING, attacker)) { //绝情效果
                entity.damage(entity.getDamageSources().genericKill(), Math.min(Math.max(7, entity.getMaxHealth() / 3), amount));
                voice(attacker, Sounds.JUEQING, 1);
                return true;
            }
        }

        return false;
    }

    private static @Nullable LivingEntity getLiuliEntity(Entity entity, LivingEntity attacker) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box box = new Box(entity.getBlockPos()).expand(10);
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity1 -> entity1 != entity && entity1 != attacker);
            if (!entities.isEmpty()) {
                Map<Float, LivingEntity> map = new HashMap<>();
                for (var e : entities) {
                    map.put(e.distanceTo(entity), e);
                }
                float min = Collections.min(map.keySet());
                return map.values().stream().toList().get(map.keySet().stream().toList().indexOf(min));
            }
        }
        return null;
    }

    private static boolean inrattan(LivingEntity entity) {return hasTrinket(ModItems.RATTAN_ARMOR, entity);}

    private static boolean baguaTrigger(LivingEntity entity) {
        return hasTrinket(ModItems.BAGUA, entity) && new Random().nextFloat() < 0.5;
    }

    private static void shan(LivingEntity entity, boolean bl) {
        ItemStack stack = bl ? ItemStack.EMPTY : shanStack(entity);
        int cd = bl ? 60 : 40;
        entity.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        if (bl) voice(entity, Sounds.BAGUA);
        voice(entity, Sounds.SHAN);
        if (entity instanceof PlayerEntity player) {
            cardUsePost(player, stack, null);
            if (bl) player.sendMessage(Text.translatable("dabaosword.bagua"),true);
        } else stack.decrement(1);
    }

    private static int getShanSlot(PlayerEntity player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != ModItems.SHAN) continue;
            return i;
        }
        return -1;
    }

    private static ItemStack shanStack(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) return player.getInventory().getStack(getShanSlot(player));
        return entity.getOffHandStack();
    }
}
