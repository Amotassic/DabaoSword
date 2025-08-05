package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.hurtByCard;
import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void trySave(LivingEntity entity, float amount) {
        for (int i = 0; i < 114; i++) {
            if (entity.isAlive()) return;
            ItemStack stack = getCard(entity, canSaveDying);
            if (!stack.isEmpty()) {
                CardItem.onUse(entity, stack, true);
                entity.setHealth(entity.getHealth() - amount + 5);
                amount -= 5;
            }
        }
    }

    private static void tiesuoTrigger(LivingEntity entity, DamageSource source, float amount) {
        World world = entity.getWorld();
        if (entity.isGlowing() && source.isIn(Tags.TRIGGER_TIESUO)) {
            entity.removeStatusEffect(StatusEffects.GLOWING);
            Box box = new Box(entity.getBlockPos()).expand(20);
            for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, box, e -> e != entity && e.isGlowing())) {
                near.removeStatusEffect(StatusEffects.GLOWING);
                near.damage(source, amount);
                if (source.isIn(DamageTypeTags.IS_FREEZING)) near.setFrozenTicks(entity.getFrozenTicks());
                if (source.isIn(DamageTypeTags.IS_FIRE)) {
                    int fireTicks = entity.getFireTicks() / 20;
                    int fireTime = fireTicks == 0 ? 6 : fireTicks;
                    near.setOnFireFor(fireTime);
                }
                if (source.isIn(DamageTypeTags.IS_LIGHTNING)) ShandianEffect.summonLightning(near, true, false);
            }
        }
    }

    @Override
    public void hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getWorld() instanceof ServerWorld) {

            tiesuoTrigger(entity, source, amount);

            getSkillOwners(entity).forEach(player ->
                    getResult(Trigger.ON_HURT, player, entity, d().withDamage(source, amount)));

            trySave(entity, amount);

            if (source.isIn(Tags.FROM_CARD)) hurtByCard(entity, source, amount);

            if (source.getAttacker() instanceof LivingEntity living) {
                if (living.getCommandTags().contains("px")) entity.timeUntilRegen = 0;

                if (living instanceof PlayerEntity && entity instanceof PlayerEntity && amount >= 15) {
                    voice(living, "wushuang");
                }
            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        draw(player);
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                }
                if (entity instanceof PlayerEntity && player!= entity) {
                    if (player.getHealth() < 20) player.heal(20 - player.getHealth());
                    draw(player);
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                }
            }

            if (entity.isDead()) getSkillOwners(entity).forEach(player ->
                    getResult(Trigger.ON_DEATH, player, entity, d().withDamage(source, amount)));

        }
    }
}
