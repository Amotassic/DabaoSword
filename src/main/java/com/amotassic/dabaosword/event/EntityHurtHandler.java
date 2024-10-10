package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void trySave(LivingEntity entity, float amount) {
        for (int i = 0; i < 1145; i++) {
            if (entity.isAlive()) return;
            boolean canSave = entity instanceof PlayerEntity player ? hasCard(player, canSaveDying) : canSaveDying.test(entity.getOffHandStack());
            if (canSave) {
                Pair<CardPileInventory, ItemStack> stack = entity instanceof PlayerEntity player ? getCard(player, canSaveDying) : new Pair<>(null, entity.getOffHandStack());
                if (stack.getRight().isOf(ModItems.PEACH))  voice(entity, Sounds.RECOVER);
                if (stack.getRight().isOf(ModItems.JIU))    voice(entity, Sounds.JIU);
                if (entity.timeUntilRegen > 9) {
                    if (entity instanceof PlayerEntity player) cardUsePost(player, stack, player);
                    else stack.getRight().decrement(1);
                }
                entity.setHealth(entity.getHealth() - amount + 5); amount -= 5;
            }
        }
    }

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getWorld() instanceof ServerWorld world) {

            for (var pair : allTrinkets(entity)) { //受伤害后触发，优先级高
                ItemStack stack = pair.getRight();
                if (stack.getItem() instanceof SkillItem skill && canTrigger(skill, entity)) skill.onHurt(stack, entity, source, amount);
                if (stack.getItem() instanceof Equipment skill) skill.onHurt(stack, entity, source, amount);
            }

            trySave(entity, amount);

            if (source.getAttacker() instanceof LivingEntity living) {
                if (living.getCommandTags().contains("px")) entity.timeUntilRegen = 0;
            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        draw(player);
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                }
                if (entity instanceof PlayerEntity) {
                    draw(player, 2);
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                }
            }

            if (source.getSource() instanceof PlayerEntity player) {
                //杀的相关结算
                if (shouldSha(player)) {
                    ItemStack stack = isSha.test(player.getMainHandStack()) ? player.getMainHandStack() : shaStack(player);
                    player.addCommandTag("sha");
                    if (stack.getItem() == ModItems.SHA) {
                        voice(player, Sounds.SHA);
                        if (!hasTrinket(ModItems.RATTAN_ARMOR, entity)) {
                            entity.timeUntilRegen = 0; entity.damage(source, 5);
                        } else voice(entity, Sounds.TENGJIA1);
                    }
                    if (stack.getItem() == ModItems.FIRE_SHA) {
                        voice(player, Sounds.SHA_FIRE);
                        entity.timeUntilRegen = 0; entity.setOnFireFor(5);
                    }
                    if (stack.getItem() == ModItems.THUNDER_SHA) {
                        voice(player, Sounds.SHA_THUNDER);
                        entity.timeUntilRegen = 0; entity.damage(player.getDamageSources().indirectMagic(player, player),5);
                        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                        if (lightningEntity != null) {
                            lightningEntity.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                            lightningEntity.setCosmetic(true);
                        }
                        world.spawnEntity(lightningEntity);
                    }
                    if (entity.isGlowing()) { //处理铁索连环的效果 铁索传导过去的伤害会触发2次加伤，这符合三国杀的逻辑，所以不改了
                        if (stack.getItem() != ModItems.SHA) entity.removeStatusEffect(StatusEffects.GLOWING);
                        Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
                        for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, entities -> entities.isGlowing() && entities != entity)) {
                            if (stack.getItem() == ModItems.FIRE_SHA) {
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING); nearbyEntity.damage(source, amount);
                                nearbyEntity.timeUntilRegen = 0; nearbyEntity.setOnFireFor(5);
                            }
                            if (stack.getItem() == ModItems.THUNDER_SHA) {
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING); nearbyEntity.damage(source, amount);
                                nearbyEntity.timeUntilRegen = 0; nearbyEntity.damage(player.getDamageSources().magic(), 5);
                                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                                if (lightningEntity != null) {
                                    lightningEntity.refreshPositionAfterTeleport(nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ());
                                    lightningEntity.setCosmetic(true);
                                }
                                world.spawnEntity(lightningEntity);
                            }
                        }
                    }
                    cardUsePost(player, new Pair<>(null, stack), entity);
                }
            }

            if (source.getSource() instanceof LivingEntity living) { //在近战攻击造成伤害后触发
                for (var pair : allTrinkets(living)) {
                    ItemStack stack = pair.getRight();
                    if (stack.getItem() instanceof SkillItem skill && canTrigger(skill, living)) skill.postAttack(stack, entity, living, amount);
                    if (stack.getItem() instanceof Equipment skill) skill.postAttack(stack, entity, living, amount);
                }
            }

            if (source.getAttacker() instanceof LivingEntity living) { //只要攻击造成伤害即可触发，包括远程
                for (var pair : allTrinkets(living)) {
                    ItemStack stack = pair.getRight();
                    if (stack.getItem() instanceof SkillItem skill && canTrigger(skill, living)) skill.postDamage(stack, entity, living, amount);
                    if (stack.getItem() instanceof Equipment skill) skill.postDamage(stack, entity, living, amount);
                }
            }

        }
        return ActionResult.PASS;
    }

    boolean shouldSha(PlayerEntity player) {
        return getShaSlot(player) != -1 && !player.getCommandTags().contains("sha") && !player.getCommandTags().contains("juedou") && !player.getCommandTags().contains("wanjian");
    }
}
