package com.amotassic.dabaosword.item.tool;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ArrowRainItem extends Item {
    public ArrowRainItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.dabaosword.wanjian.tooltip"));
    }

    //一次射五发
    @Override
    public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getMainHandStack();
        if (hand == Hand.MAIN_HAND && !world.isClient()) {
            arrowRain(playerEntity, 5, 5);
            if (!playerEntity.isCreative()) stack.damage(1, playerEntity, EquipmentSlot.MAINHAND);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    public static void arrowRain(LivingEntity entity, float speed, int count) {
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
        for (int i = 0; i < count; i++) {
            int j;
            if (i % 2 == 0) j = -5 * i / 2; else j = 5 * (i + 1) / 2;
            summonArrow(entity, j, speed);
        }
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonArrow(LivingEntity entity, int angle, float speed) {
        ItemStack stack = new ItemStack(Items.ARROW);
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
        ArrowEntity arrow = new ArrowEntity(world, entity, stack, null);
        arrow.addCommandTag("a");
        arrow.setVelocity(entity, entity.getPitch(), entity.getYaw() + angle, 0.0F, speed, 1.0F);
        arrow.setCritical(true);
        world.spawnEntity(arrow);
    }

    //effect give @e[type=minecraft:iron_golem,limit=1,sort=nearest] dabaosword:cooldown2 1 4 true
    public static void arrowAround(LivingEntity entity, float speed, int count, double radius, double height) {
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
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
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonArrowToEntity(LivingEntity target, double x, double y, double z, float speed) {
        ServerWorld world = (ServerWorld) target.getEntityWorld();
        ArrowEntity arrow = new ArrowEntity(world, x, y, z, new ItemStack(Items.ARROW), null);
        arrow.addCommandTag("cosmetic");
        // 计算箭的速度向量
        double dx = target.getX() - x;
        double dy = target.getY() + target.getEyeHeight(target.getPose()) - y;
        double dz = target.getZ() - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        arrow.setVelocity(dx / distance * speed, dy / distance * speed, dz / distance * speed, speed, 1.0F);
        arrow.setCritical(true);
        world.spawnEntity(arrow);
    }

    public static void tridentStorm(LivingEntity entity, float speed, int count, double radius, double height) {
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
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
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonTridentToEntity(LivingEntity target, double x, double y, double z, float speed) {
        ServerWorld world = (ServerWorld) target.getEntityWorld();
        TridentEntity trident = new TridentEntity(world, target, new ItemStack(Items.TRIDENT));
        trident.addCommandTag("a");
        double dx = target.getX() - x;
        double dy = target.getY() + target.getEyeHeight(target.getPose()) - y;
        double dz = target.getZ() - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        world.spawnEntity(trident);
        trident.setVelocity(dx / distance * speed, dy / distance * speed, dz / distance * speed, speed, 1.0F);
        trident.setCritical(true);
        trident.refreshPositionAfterTeleport(x, y, z);
    }
}
