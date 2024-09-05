package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.card.CardItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ArrowRainItem extends CardItem {
    public ArrowRainItem(Settings settings) {super(settings);}
    //一次射五发
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            arrowRain(playerEntity, 5);
            if (!playerEntity.isCreative()) stack.damage(1, playerEntity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    public static void arrowRain(LivingEntity entity, float speed) {
        ServerWorld world = (ServerWorld) entity.getWorld();
        int[] angles = {10, 5, 0, -5, -10};
        for (int angle : angles) {summonArrow(entity, angle, speed);}
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static void summonArrow(LivingEntity entity, int angle, float speed) {
        ServerWorld world = (ServerWorld) entity.getWorld();
        ArrowEntity arrow = new ArrowEntity(world, entity);
        arrow.setCustomName(Text.of("a"));
        arrow.setVelocity(entity, entity.getPitch(), entity.getYaw() + angle, 0.0F, speed, 1.0F);
        arrow.setCritical(true);
        world.spawnEntity(arrow);
    }
}
