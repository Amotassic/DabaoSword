package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Gamerule;
import com.amotassic.dabaosword.util.ModConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
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
            long time = world.getTime();
            if (time % 2 == 0) {
                String[] tags = {"sha", "juedou", "nanman", "wanjian", "benxi"};
                for (var tag : tags) entity.getCommandTags().remove(tag);
            }
            if (time % 20 == 0) {
                //阳光开朗的笑容的效果：让与其对视的实体冻伤（阴风阵阵）
                if (entity.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.SUNSHINE_SMILE)) for (var e : world.getEntitiesByClass(LivingEntity.class, new Box(entity.getBlockPos()).expand(20), e -> e != entity && isEyeContact(e, entity, 25))) e.setFrozenTicks(240);
            }
            if (time % 200 == 0) {
                entity.getCommandTags().remove("seen_skill_tip");
            }

            //处理所有加触及距离和近战防御距离的效果
            int level1 = 0; int level2 = 0;
            ItemStack mainHand = entity.getMainHandStack();
            if (mainHand.isOf(ModItems.DISCARD) || mainHand.isOf(ModItems.JUEDOU)) level1 += 114;
            for (var skill : getSkillsMayUse(entity)) {
                level1 += skill.item.getExtraReach(entity, skill);
                level2 += skill.item.getDefend(entity, skill);
            }
            if (level1 > 0) entity.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 2,level1 - 1,false,false,false));
            if (level2 > 0) entity.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 2,level2 - 1,false,false,false));

            //若方天画戟被触发了，只要左键就可以造成群伤
            PlayerEntity closest = world.getClosestPlayer(entity, 5);
            ItemStack stack;
            if (closest != null && !(stack = trinketItem(ModItems.FANGTIAN, closest)).isEmpty() && entity.isAlive()) {
                if (s(stack).getCD() > 15 && closest.handSwingTicks == 1) {
                    //给玩家本人一个极短的无敌效果，以防止被误伤
                    closest.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,2,0,false,false,false));
                    float i = (float) closest.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    entity.damage(closest.getDamageSources().playerAttack(closest), i);
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
                            player.kill();
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
                        for (var skill : getSkillsMayUse(player)) {
                            int i = skill.item.onDrawPhase(player, skill);
                            if (i <= -114) {draw = 0; break;}
                            draw += i;
                        }
                        if (draw > 0) draw(player, draw);
                    }
                }
            }

            if (time % 2 == 0) decreaseAttackRange(player);

            //下落攻击触发：脚底下两格是空气，手里拿着有耐久度的物品左键即可触发
            BlockPos blockPos = player.getBlockPos().down(1); BlockPos blockPos2 = player.getBlockPos().down(2);
            if (ModConfig.FallingAttack && world.getBlockState(blockPos).getBlock() == Blocks.AIR && world.getBlockState(blockPos2).getBlock() == Blocks.AIR && player.getMainHandStack().isDamageable() && player.handSwingTicks == 1) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.FALLING_ATTACK, StatusEffectInstance.INFINITE,0,false,false,false));
            }

        }
    }

    private void decreaseAttackRange(LivingEntity entity) {
        Box box = new Box(entity.getBlockPos()).expand(20);
        for (LivingEntity target : entity.getWorld().getEntitiesByClass(LivingEntity.class, box, living -> living != entity && living.hasStatusEffect(ModItems.DEFEND) && isLooking(entity, living))) {
            //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
            int amplifier = Objects.requireNonNull(target.getStatusEffect(ModItems.DEFEND)).getAmplifier();
            entity.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 2, amplifier,false,false,true));
        }
    }

    public static boolean isLooking(LivingEntity entity, Entity target) {
        Vec3d playerPos = entity.getEyePos();
        Vec3d lookVec = entity.getRotationVec(1.0F);
        Box targetBox = target.getBoundingBox();
        // 进行射线与碰撞箱的相交检测
        return targetBox.raycast(playerPos, playerPos.add(lookVec.multiply(100.0))).isPresent();
    }

    public static boolean isEyeContact(Entity entity1, Entity entity2, float angle) {
        double MAX_ANGLE = Math.toRadians(angle);
        Vec3d pos1 = entity1.getPos(); Vec3d pos2 = entity2.getPos();
        // 计算从生物 1 到生物 2 的向量
        Vec3d d = pos2.subtract(pos1);
        // 获取生物的视线方向
        Vec3d v1 = entity1.getRotationVec(1.0F); Vec3d v2 = entity2.getRotationVec(1.0F);
        // 计算夹角
        double theta1 = Math.acos(v1.dotProduct(d.normalize()));
        double theta2 = Math.acos(v2.dotProduct(d.normalize().negate()));
        // 判断夹角是否在允许范围内
        return theta1 <= MAX_ANGLE && theta2 <= MAX_ANGLE;
    }
}
