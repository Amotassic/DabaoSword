package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.card.CardItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        ItemStack stack = playerEntity.getMainHandStack();
        if (hand == Hand.MAIN_HAND && !world.isClient) {
            arrowRain(playerEntity);
            if (!playerEntity.isCreative()) stack.damage(1, playerEntity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    public static void arrowRain(PlayerEntity player) {
        ItemStack stack = new ItemStack(Items.ARROW);
        Text a = Text.of("a");
        ServerWorld world = (ServerWorld) player.getWorld();
        ArrowEntity arrow1 = new ArrowEntity(world, player, stack);arrow1.setCustomName(a);
        ArrowEntity arrow2 = new ArrowEntity(world, player, stack);arrow2.setCustomName(a);
        ArrowEntity arrow3 = new ArrowEntity(world, player, stack);arrow3.setCustomName(a);
        ArrowEntity arrow4 = new ArrowEntity(world, player, stack);arrow4.setCustomName(a);
        ArrowEntity arrow5 = new ArrowEntity(world, player, stack);arrow5.setCustomName(a);
        arrow1.setVelocity(player, player.getPitch(), player.getYaw()+10, 0.0F, 5.0F, 1.0F);
        arrow2.setVelocity(player, player.getPitch(), player.getYaw()+5, 0.0F, 5.0F, 1.0F);
        arrow3.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 5.0F, 1.0F);
        arrow4.setVelocity(player, player.getPitch(), player.getYaw()-5, 0.0F, 5.0F, 1.0F);
        arrow5.setVelocity(player, player.getPitch(), player.getYaw()-10, 0.0F, 5.0F, 1.0F);
        arrow1.setCritical(true);arrow2.setCritical(true);arrow3.setCritical(true);arrow4.setCritical(true);arrow5.setCritical(true);
        world.spawnEntity(arrow1);world.spawnEntity(arrow2);world.spawnEntity(arrow3);world.spawnEntity(arrow4);world.spawnEntity(arrow5);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }
}
