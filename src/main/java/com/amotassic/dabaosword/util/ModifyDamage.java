package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ModifyDamage {
    //由于fabric没有提供修改伤害的方法，因此我自己注入"applyArmorToDamage"以修改生物受到的伤害值
    public static float modify(LivingEntity entity, DamageSource source, float value) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        boolean noArmor = head.isEmpty() && chest.isEmpty() && legs.isEmpty() && feet.isEmpty();

        if (source.getSource() instanceof LivingEntity attacker) {
            if (noArmor || hasTrinket(SkillCards.POJUN, attacker)) {
                //古锭刀对没有装备的生物伤害增加 限定版翻倍 卡牌版加5
                if (attacker.getMainHandStack().getItem() == ModItems.GUDINGDAO) value += value;
                if (hasTrinket(ModItems.GUDING_WEAPON, attacker)) value += 5;
            }

            //排异技能：攻击伤害增加
            if (hasTrinket(SkillCards.QUANJI, attacker)) {
                ItemStack stack = trinketItem(SkillCards.QUANJI, attacker);
                int quan = getTag(stack);
                if (quan > 0) {
                    if (quan > 4 && entity instanceof PlayerEntity player) draw(player, 2);
                    setTag(stack, quan/2);
                    voice(attacker, Sounds.PAIYI);
                    value += quan;
                }
            }

            //烈弓：命中后加伤害，至少为5
            if (hasTrinket(SkillCards.LIEGONG, attacker) && !attacker.hasStatusEffect(ModItems.COOLDOWN)) {
                float f = Math.max(13 - attacker.distanceTo(entity), 5);
                attacker.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, (int) (40 * f),0,false,false,true));
                voice(attacker, Sounds.LIEGONG);
                value += f;
            }
        }

        //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！（伤害大于5就只加5）
        if (source.isIn(DamageTypeTags.IS_FIRE) && hasTrinket(ModItems.RATTAN_ARMOR, entity)) value += value > 5 ? 5 : value;

        //白银狮子减伤
        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.getAttacker() instanceof LivingEntity && hasTrinket(ModItems.BAIYIN, entity)) value *= 0.4f;

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            entity.damageArmor(source, value);
            value = DamageUtil.getDamageLeft(value, entity.getArmor(), (float)entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return value;
    }

    public static boolean shouldCancel(LivingEntity entity, DamageSource source, float amount) {
        //无敌效果
        if (entity.hasStatusEffect(ModItems.INVULNERABLE) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        //弹射物对藤甲无效
        if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(entity)) {
            if (source.getSource() != null) source.getSource().discard();
            return true;
        }

        if (source.getSource() instanceof LivingEntity sourceEntity) {
            //沈佳宜防御效果
            if (!(sourceEntity instanceof PlayerEntity) && entity.hasStatusEffect(ModItems.DEFEND)) {
                if (Objects.requireNonNull(entity.getStatusEffect(ModItems.DEFEND)).getAmplifier() >= 2) return true;
            }

            //若攻击者主手没有物品，则无法击穿藤甲
            if (inrattan(entity) && sourceEntity.getMainHandStack().isEmpty()) return true;

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
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, null);
                    dog.setHealth(0);
                    return true;
                }
                dog.setHealth(0);
            }
        }

        if (source.getAttacker() instanceof LivingEntity attacker) {

            //翻面的生物（除了玩家）无法造成伤害
            if (!(attacker instanceof PlayerEntity) && attacker.hasStatusEffect(ModItems.TURNOVER)) return true;

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
                        CardDiscardCallback.EVENT.invoker().cardDiscard(player, stack, 1, false);
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
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, entity);
                }
                return true;
            }

            if (hasTrinket(SkillCards.JUEQING, attacker)) { //绝情效果
                entity.damage(entity.getDamageSources().genericKill(), Math.min(7, amount));
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
        voice(entity, Sounds.SHAN);
        if (entity instanceof PlayerEntity player) {
            CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, null);
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
