package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.hurtByCard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void trySave(LivingEntity entity, float amount) {
        for (int i = 0; i < 114; i++) {
            if (entity.isAlive()) return;
            ItemStack stack = getCard(entity, canSaveDying);
            if (!stack.isEmpty()) {
                CardItem.onUse(entity, stack, null, true);
                entity.setHealth(entity.getHealth() - amount + 5);
                amount -= 5;
            }
        }
    }

    private static void tiesuoTrigger(LivingEntity entity, DamageSource source, float amount) {
        ServerLevel world = world(entity);
        if (entity.isCurrentlyGlowing() && source.is(Tags.TRIGGER_TIESUO)) {
            entity.removeEffect(MobEffects.GLOWING);
            AABB box = new AABB(entity.getOnPos()).inflate(20);
            for (LivingEntity near : world.getEntitiesOfClass(LivingEntity.class, box, e -> e != entity && e.isCurrentlyGlowing())) {
                near.removeEffect(MobEffects.GLOWING);
                near.hurtServer(world, source, amount);
                if (source.is(DamageTypeTags.IS_FREEZING)) near.setTicksFrozen(entity.getTicksFrozen());
                if (source.is(DamageTypeTags.IS_FIRE)) {
                    int fireTicks = entity.getRemainingFireTicks();
                    int fireTime = fireTicks == 0 ? 120 : fireTicks;
                    near.setRemainingFireTicks(fireTime);
                }
                if (source.is(DamageTypeTags.IS_LIGHTNING)) ShandianEffect.summonLightning(near, true, false);
            }
        }
    }

    @Override
    public void hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.level() instanceof ServerLevel) {

            tiesuoTrigger(entity, source, amount);

            getSkillOwners(entity).forEach(player ->
                    getResult(Trigger.ON_HURT, player, entity, d().withDamage(source, amount)));

            trySave(entity, amount);

            if (source.is(Tags.FROM_CARD)) hurtByCard(entity, source, amount);

            if (source.getEntity() instanceof LivingEntity living) {
                if (living.entityTags().contains("px")) entity.invulnerableTime = 0;

                if (living instanceof Player && entity instanceof Player && amount >= 15) {
                    voice(living, "wushuang");
                }
            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getEntity() instanceof Player player && entity.getHealth() <= 0) {
                if (entity instanceof Monster) {
                    if (new Random().nextFloat() < 0.1) {
                        draw(player);
                        player.sendOverlayMessage(Component.translatable("dabaosword.draw.monster"));
                    }
                }
                if (entity instanceof Player && player!= entity) {
                    if (player.getHealth() < 20) player.heal(20 - player.getHealth());
                    draw(player);
                    player.sendOverlayMessage(Component.translatable("dabaosword.draw.player"));
                }
            }

            if (entity.isDeadOrDying()) getSkillOwners(entity).forEach(player ->
                    getResult(Trigger.ON_DEATH, player, entity, d().withDamage(source, amount)));

        }
    }
}
