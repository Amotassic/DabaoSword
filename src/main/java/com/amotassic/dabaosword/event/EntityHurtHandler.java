package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.effect.ShandianEffect;
import com.amotassic.dabaosword.item.ModItems;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.Random;

import static com.amotassic.dabaosword.api.event.CardEvents.cardUsePost;
import static com.amotassic.dabaosword.api.event.CardEvents.hurtBy;
import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void trySave(LivingEntity entity, float amount) {
        for (int i = 0; i < 114; i++) {
            if (entity.isAlive()) return;
            if (hasCard(entity, canSaveDying)) {
                ItemStack stack = getCard(entity, canSaveDying).getRight();
                cardUsePost(entity, stack, entity);
                entity.setHealth(entity.getHealth() - amount + 5); amount -= 5;
            }
        }
    }

    private static void tiesuoTrigger(LivingEntity entity, DamageSource source, float amount) {
        ServerWorld world = world(entity);
        if (entity.isGlowing() && source.isIn(Tags.TRIGGER_TIESUO)) {
            entity.removeStatusEffect(StatusEffects.GLOWING);
            Box box = new Box(entity.getBlockPos()).expand(20);
            for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, box, e -> e != entity && e.isGlowing())) {
                near.removeStatusEffect(StatusEffects.GLOWING);
                near.damage(world, source, amount);
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
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {

        tiesuoTrigger(entity, source, amount);

        for (var stack : allTrinkets(entity)) { //受伤害后触发，优先级高
            if (stack.getItem() instanceof Skill skill && canTrigger(stack, entity)) skill.onHurt(stack, entity, source, amount);
        }

        trySave(entity, amount);

        if (isWanjian(source)) hurtBy(entity, ModItems.WANJIAN);
        if (isHuogong(source)) hurtBy(entity, ModItems.FIRE_ATTACK);
        if (isShandian(source)) hurtBy(entity, ModItems.SHANDIAN_ITEM);

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
            if (entity instanceof PlayerEntity && player!= entity) {
                if (player.getHealth() < 20) player.heal(20 - player.getHealth());
                draw(player);
                player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
            }
        }

        if (source.getSource() instanceof LivingEntity living) { //在近战攻击造成伤害后触发
            for (var stack : allTrinkets(living)) {
                if (stack.getItem() instanceof Skill skill && canTrigger(stack, living)) skill.postAttack(stack, entity, living, amount);
            }
        }

        if (source.getAttacker() instanceof LivingEntity living) { //只要攻击造成伤害即可触发，包括远程
            for (var stack : allTrinkets(living)) {
                if (stack.getItem() instanceof Skill skill && canTrigger(stack, living)) skill.postDamage(stack, entity, living, amount);
            }
        }
        return ActionResult.PASS;
    }
}
