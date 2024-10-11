package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Gamerule;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityTickEvents implements EndEntityTick.EndLivingTick, EndEntityTick.EndPlayerTick {
    @Override
    public void endLivingTick(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (world.getTime() % 2 == 0) {
                entity.getCommandTags().remove("sha");
                entity.getCommandTags().remove("juedou");
            }

            //若方天画戟被触发了，只要左键就可以造成群伤
            PlayerEntity closestPlayer = world.getClosestPlayer(entity, 5);
            if (closestPlayer != null && hasTrinket(ModItems.FANGTIAN, closestPlayer) && entity.isAlive()) {
                ItemStack stack = trinketItem(ModItems.FANGTIAN, closestPlayer);
                int time = 0;
                if (stack != null) time = getCD(stack);
                if (time > 15 && closestPlayer.handSwingTicks == 1) {
                    //给玩家本人一个极短的无敌效果，以防止被误伤
                    closestPlayer.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,2,0,false,false,false));
                    float i = (float) closestPlayer.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    entity.damage(closestPlayer.getDamageSources().playerAttack(closestPlayer), i);
                }
            }
        }
    }

    @Override
    public void endPlayerTick(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            var time = world.getTime();
            int giveCard = world.getGameRules().getInt(Gamerule.GIVE_CARD_INTERVAL) * 20;
            int skill = world.getGameRules().getInt(Gamerule.CHANGE_SKILL_INTERVAL) * 20;
            boolean limit = world.getGameRules().getBoolean(Gamerule.ENABLE_CARDS_LIMIT);

            if (time % giveCard == 0) { // 每分钟摸两张牌
                if (hasTrinket(ModItems.CARD_PILE, player) && !player.isCreative() && !player.isSpectator() && player.isAlive()) {
                    if (countCards(player) < player.getMaxHealth() || !limit) {
                        draw(player, 2);
                        player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    }
                }
            }

            if (skill >= 0) {
                if (skill == 0) player.addCommandTag("change_skill");
                else if (time % skill == 0) { //每5分钟可以切换技能
                    player.addCommandTag("change_skill");
                    if (skill >= 600 && hasTrinket(ModItems.CARD_PILE, player)) {
                        player.sendMessage(Text.translatable("dabaosword.change_skill").formatted(Formatting.BOLD));
                        player.sendMessage(Text.translatable("dabaosword.change_skill2"));
                    }
                }
            }

            if (time % 2 == 0) {
                player.getCommandTags().remove("benxi");
                player.getCommandTags().remove("xingshang");

                //牌堆恢复饱食度
                boolean food = world.getGameRules().getBoolean(Gamerule.CARD_PILE_HUNGERLESS);
                if (hasTrinket(ModItems.CARD_PILE, player) && food) player.getHungerManager().setFoodLevel(20);
            }

            Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
            for (LivingEntity nearbyPlayer : world.getEntitiesByClass(PlayerEntity.class, box, playerEntity -> playerEntity.hasStatusEffect(ModItems.DEFEND))) {
                //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
                int amplifier = Objects.requireNonNull(nearbyPlayer.getStatusEffect(ModItems.DEFEND)).getAmplifier();
                int attack = (int) (player.getAttributeValue(ReachEntityAttributes.ATTACK_RANGE) + 3);
                int defended = Math.min(amplifier, attack);
                if (player != nearbyPlayer && isLooking(player, nearbyPlayer)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 1, defended,false,false,true));
                }
            }

            int level1 = 0; int level2 = 0; //马术和飞影的效果
            if (shouldMashu(player)) {
                if (hasTrinket(ModItems.CHITU, player)) level1++;
                if (hasTrinket(SkillCards.MASHU, player)) level1++;
                if (level1 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,level1,false,false,true));
            }
            if (hasTrinket(ModItems.DILU, player)) level2++;
            if (hasTrinket(SkillCards.FEIYING, player)) level2++;
            if (level2 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,level2,false,false,true));

            //下落攻击触发：脚底下两格是空气，手里拿着有耐久度的物品左键即可触发
            BlockPos blockPos = player.getBlockPos().down(1); BlockPos blockPos2 = player.getBlockPos().down(2);
            boolean falling = world.getGameRules().getBoolean(Gamerule.ENABLE_FALLING_ATTACK);
            if (falling && world.getBlockState(blockPos).getBlock() == Blocks.AIR && world.getBlockState(blockPos2).getBlock() == Blocks.AIR && player.getMainHandStack().getItem().isDamageable() && player.handSwingTicks == 1) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.FALLING_ATTACK, StatusEffectInstance.INFINITE,0,false,false,false));
            }

        }
    }

    boolean shouldMashu(PlayerEntity player) {
        return !hasTrinket(SkillCards.BENXI, player) && player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }

    boolean isLooking(PlayerEntity player, Entity entity) {
        Vec3d vec3d = player.getRotationVec(1.0f).normalize();
        Vec3d vec3d2 = new Vec3d(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d = vec3d2.length();
        double e = vec3d.dotProduct(vec3d2.normalize());
        if (e > 1.0 - 0.25 / d) return player.canSee(entity);
        return false;
    }
}
