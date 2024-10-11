package com.amotassic.dabaosword.event;

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
import net.minecraft.util.math.Box;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void trySave(LivingEntity entity, float amount) {
        for (int i = 0; i < 1145; i++) {
            if (entity.isAlive()) return;
            if (hasCard(entity, canSaveDying)) {
                ItemStack stack = getCard(entity, canSaveDying).getRight();
                if (stack.isOf(ModItems.PEACH))  voice(entity, Sounds.RECOVER);
                if (stack.isOf(ModItems.JIU))    voice(entity, Sounds.JIU);
                if (entity.timeUntilRegen > 9) nonPreUseCardDecrement(entity, stack, entity);
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

            if (source.getSource() instanceof LivingEntity SE) {
                //杀的相关结算
                if (shouldSha(SE) && entity.isAlive()) {
                    ItemStack stack = isSha.test(SE.getMainHandStack()) ? SE.getMainHandStack() : getItem(SE, isSha);
                    ItemStack sha = stack.copy();
                    //处理铁索连环的效果 铁索传导过去的伤害会触发2次加伤，这符合三国杀的逻辑，所以不改了
                    if (cardUsePre(SE, stack, entity) && entity.isGlowing()) {
                        if (!sha.isOf(ModItems.SHA)) entity.removeStatusEffect(StatusEffects.GLOWING);
                        Box box = new Box(SE.getBlockPos()).expand(20); // 检测范围，根据需要修改
                        for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, box, e -> e.isGlowing() && e != entity)) {
                            if (sha.isOf(ModItems.FIRE_SHA)) {
                                near.removeStatusEffect(StatusEffects.GLOWING); near.damage(source, amount);
                                near.timeUntilRegen = 0; near.setOnFireFor(5);
                            }
                            if (sha.isOf(ModItems.THUNDER_SHA)) {
                                near.removeStatusEffect(StatusEffects.GLOWING); near.damage(source, amount);
                                near.timeUntilRegen = 0; near.damage(SE.getDamageSources().magic(), 5);
                                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                                if (lightningEntity != null) {
                                    lightningEntity.refreshPositionAfterTeleport(near.getX(), near.getY(), near.getZ());
                                    lightningEntity.setCosmetic(true);
                                }
                                world.spawnEntity(lightningEntity);
                            }
                        }
                    }
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

    boolean shouldSha(LivingEntity entity) {
        return hasItem(entity, isSha) && !entity.getCommandTags().contains("sha") && !entity.getCommandTags().contains("juedou");
    }
}
