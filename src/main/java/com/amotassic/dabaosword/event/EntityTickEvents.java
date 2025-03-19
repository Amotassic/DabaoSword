package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.ReachDefend;
import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityTickEvents implements EndEntityTick.EndLivingTick, EndEntityTick.EndPlayerTick {
    @Override
    public void endLivingTick(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (world.getTime() % 2 == 0) {
                entity.getCommandTags().remove("sha");
                entity.getCommandTags().remove("juedou");
                entity.getCommandTags().remove("nanman");
                entity.getCommandTags().remove("benxi");
                entity.getCommandTags().remove("xingshang");
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
                    float i = (float) closestPlayer.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
                    entity.damage(world, closestPlayer.getDamageSources().playerAttack(closestPlayer), i);
                }
            }
        }
    }

    @Override
    public void endPlayerTick(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            var time = world.getTime();
            int giveCard = world.getGameRules().getInt(Gamerule.GIVE_CARD_INTERVAL) * 20;
            boolean limit = world.getGameRules().getBoolean(Gamerule.ENABLE_CARDS_LIMIT);

            if (time % 100 == 0) {
                //若玩家不在任何一场对战中，且拥有身份标签，则移除。如果意外卡在旁观者模式，变回生存模式并杀死
                if (!PVPGameEvents.getGameManager().isPlayerInGame(player)) {
                    var tags = player.getCommandTags();
                    if (tags.contains("dabaosword.zhong") || tags.contains("dabaosword.fan") || tags.contains("dabaosword.nei")) {
                        player.getCommandTags().remove("dabaosword.zhong");
                        player.getCommandTags().remove("dabaosword.fan");
                        player.getCommandTags().remove("dabaosword.nei");
                        if (player.isSpectator()) {
                            ((ServerPlayerEntity) player).changeGameMode(GameMode.SURVIVAL);
                            player.kill(world);
                        }
                    }
                }
            }

            if (time % giveCard == 0) { // 每分钟摸两张牌
                if (!player.isCreative() && !player.isSpectator() && player.isAlive()) {
                    player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    if (player.hasStatusEffect(ModItems.BINGLIANG)) player.removeStatusEffect(ModItems.BINGLIANG);
                    else if (countCards(player) < player.getMaxHealth() || !limit) {
                        int draw = hasTrinket(ModItems.CARD_PILE, player) ? 2 : 0;
                        for (var stack : allTrinkets(player)) {
                            if (stack.getItem() instanceof Skill s && canTrigger(stack, player)) {
                                int i = s.onDrawPhase(player, stack);
                                if (i <= -114) {draw = 0; break;}
                                draw += i;
                            }
                        }
                        if (draw > 0) draw(player, draw);
                    }
                }
            }

            Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
            for (LivingEntity nearbyPlayer : world.getEntitiesByClass(PlayerEntity.class, box, playerEntity -> playerEntity.hasStatusEffect(ModItems.DEFEND))) {
                //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
                int amplifier = Objects.requireNonNull(nearbyPlayer.getStatusEffect(ModItems.DEFEND)).getAmplifier();
                int attack = (int) player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE);
                int defended = Math.min(amplifier, attack);
                if (player != nearbyPlayer && isLooking(player, nearbyPlayer)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 1, defended,false,false,true));
                }
            }

            //处理所有加触及距离和近战防御距离的效果
            int level1 = 0; int level2 = 0;
            ItemStack mainHand = player.getMainHandStack();
            if (mainHand.isOf(ModItems.DISCARD) || mainHand.isOf(ModItems.JUEDOU)) level1 += 114;
            for (var stack : allTrinkets(player)) {
                if (stack.getItem() instanceof ReachDefend rd && canTrigger(stack, player)) {
                    level1 += rd.getExtraReach(player, stack);
                    level2 += rd.getDefend(player, stack);
                }
            }
            if (level1 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 2,level1 - 1,false,false,false));
            if (level2 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 2,level2 - 1,false,false,false));

            //下落攻击触发：脚底下两格是空气，手里拿着有耐久度的物品左键即可触发
            BlockPos blockPos = player.getBlockPos().down(1); BlockPos blockPos2 = player.getBlockPos().down(2);
            if (ModConfig.FallingAttack && world.getBlockState(blockPos).getBlock() == Blocks.AIR && world.getBlockState(blockPos2).getBlock() == Blocks.AIR && player.getMainHandStack().isDamageable() && player.handSwingTicks == 1) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.FALLING_ATTACK, StatusEffectInstance.INFINITE,0,false,false,false));
            }

        }
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
