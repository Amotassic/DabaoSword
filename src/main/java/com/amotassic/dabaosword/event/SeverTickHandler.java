package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class SeverTickHandler implements ServerTickEvents.EndTick, ModTools {
    private int tick = 0;
    private int tick2 = 0;
    private int skillChange = 0;
    @Override
    public void onEndTick(MinecraftServer server) {

        if (++tick >= 1200) { // 每分钟摸两张牌
            tick = 0;
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    if (hasTrinket(ModItems.CARD_PILE, player) && !player.isCreative() && !player.isSpectator()) {
                        player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                        player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    }
                }
            }
        }

        if (++skillChange >= 6000) {//每5分钟可以切换技能
            skillChange = 0;
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    player.addCommandTag("change_skill");
                    player.sendMessage(Text.translatable("dabaosword.change_skill").formatted(Formatting.BOLD));
                    player.sendMessage(Text.translatable("dabaosword.change_skill2"));
                }
            }
        }

        if (++tick2 >= 2) {
            tick2 = 0;
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    player.getCommandTags().remove("quanji");
                    player.getCommandTags().remove("sha");
                    player.getCommandTags().remove("benxi");
                    player.getCommandTags().remove("juedou");
                }
            }
        }

        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity player : world.getPlayers()) {

                Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
                for (LivingEntity nearbyPlayer : world.getEntitiesByClass(PlayerEntity.class, box, playerEntity -> playerEntity.hasStatusEffect(ModItems.DEFEND))) {
                    //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
                    int amplifier = Objects.requireNonNull(nearbyPlayer.getStatusEffect(ModItems.DEFEND)).getAmplifier();
                    int attack = (int) (player.getAttributeValue(ReachEntityAttributes.ATTACK_RANGE) + (player.isCreative()?6:3));
                    int defensed = Math.min(amplifier, attack);
                    if (player != nearbyPlayer && islooking(player, nearbyPlayer)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 1,defensed,false,false,false));
                    }
                }

                //马术和飞影的效果
                if (shouldMashu(player)) {
                    if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,2));
                    } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1));
                    }
                }
                if (hasTrinket(ModItems.DILU, player) && hasTrinket(SkillCards.FEIYING, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,2));
                } else if (hasTrinket(ModItems.DILU, player) || hasTrinket(SkillCards.FEIYING, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,1));
                }

            }
        }
    }

    boolean shouldMashu(PlayerEntity player) {
        return !hasTrinket(SkillCards.BENXI, player) && player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }

    boolean islooking(PlayerEntity player, Entity entity) {
        Vec3d vec3d = player.getRotationVec(1.0f).normalize();
        Vec3d vec3d2 = new Vec3d(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d = vec3d2.length();
        double e = vec3d.dotProduct(vec3d2.normalize());
        if (e > 1.0 - 0.25 / d) {
            return player.canSee(entity);
        }
        return false;
    }
}
