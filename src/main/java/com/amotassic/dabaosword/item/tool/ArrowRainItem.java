package com.amotassic.dabaosword.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ArrowRainItem extends Item {
    public ArrowRainItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.wanjian.tooltip"));
    }

    //一次射五发
    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player playerEntity, @NonNull InteractionHand hand) {
        ItemStack stack = playerEntity.getMainHandItem();
        if (hand == InteractionHand.MAIN_HAND && !world.isClientSide()) {
            arrowRain(playerEntity, 5, 5);
            if (!playerEntity.isCreative()) stack.hurtAndBreak(1, playerEntity, EquipmentSlot.MAINHAND);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    public static void arrowRain(LivingEntity entity, float speed, int count) {
        ServerLevel world = (ServerLevel) entity.level();
        for (int i = 0; i < count; i++) {
            int j;
            if (i % 2 == 0) j = -5 * i / 2; else j = 5 * (i + 1) / 2;
            summonArrow(entity, j, speed);
        }
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonArrow(LivingEntity entity, int angle, float speed) {
        ItemStack stack = new ItemStack(Items.ARROW);
        ServerLevel world = (ServerLevel) entity.level();
        Arrow arrow = new Arrow(world, entity, stack, null);
        arrow.addTag("a");
        arrow.shootFromRotation(entity, entity.getXRot(), entity.getYRot() + angle, 0.0F, speed, 1.0F);
        arrow.setCritArrow(true);
        world.addFreshEntity(arrow);
    }

    //effect give @e[type=minecraft:iron_golem,limit=1,sort=nearest] dabaosword:cooldown2 1 4 true
    public static void arrowAround(LivingEntity entity, float speed, int count, double radius, double height) {
        ServerLevel world = (ServerLevel) entity.level();
        for (int i = 0; i < count; i++) {
            // 计算角度
            float angle = (float) (i * (360.0 / count));
            // 将角度转换为弧度
            double radians = Math.toRadians(angle);
            // 计算箭的生成位置
            double xOffset = Math.cos(radians) * radius;
            double zOffset = Math.sin(radians) * radius;
            double x = entity.getX() + xOffset;
            double z = entity.getZ() + zOffset;
            double y = entity.getY() + height;
            summonArrowToEntity(entity, x, y, z, speed);
        }
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonArrowToEntity(LivingEntity target, double x, double y, double z, float speed) {
        ServerLevel world = (ServerLevel) target.level();
        Arrow arrow = new Arrow(world, x, y, z, new ItemStack(Items.ARROW), null);
        arrow.addTag("cosmetic");
        // 计算箭的速度向量
        double dx = target.getX() - x;
        double dy = target.getY() + target.getEyeHeight(target.getPose()) - y;
        double dz = target.getZ() - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        arrow.shoot(dx / distance * speed, dy / distance * speed, dz / distance * speed, speed, 1.0F);
        arrow.setCritArrow(true);
        world.addFreshEntity(arrow);
    }

    public static void tridentStorm(LivingEntity entity, float speed, int count, double radius, double height) {
        ServerLevel world = (ServerLevel) entity.level();
        for (int i = 0; i < count; i++) {
            float angle = (float) (i * (360.0 / count));
            double radians = Math.toRadians(angle);
            double xOffset = Math.cos(radians) * radius;
            double zOffset = Math.sin(radians) * radius;
            double x = entity.getX() + xOffset;
            double z = entity.getZ() + zOffset;
            double y = entity.getY() + height;
            summonTridentToEntity(entity, x, y, z, speed);
        }
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonTridentToEntity(LivingEntity target, double x, double y, double z, float speed) {
        ServerLevel world = (ServerLevel) target.level();
        ThrownTrident trident = new ThrownTrident(world, target, new ItemStack(Items.TRIDENT));
        trident.addTag("a");
        double dx = target.getX() - x;
        double dy = target.getY() + target.getEyeHeight(target.getPose()) - y;
        double dz = target.getZ() - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        world.addFreshEntity(trident);
        trident.shoot(dx / distance * speed, dy / distance * speed, dz / distance * speed, speed, 1.0F);
        trident.setCritArrow(true);
        trident.teleportTo(x, y, z);
    }
}
