package com.amotassic.dabaosword.items.card;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ArrowRainItem extends CardItem {
    public ArrowRainItem(Settings settings) {
        super(settings);
    }
    //有大病的工具提示
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        if(Screen.hasShiftDown()){
            int i = (int) (System.currentTimeMillis() / 1000) % 7;
            if (i==0) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip7").formatted(Formatting.BLUE));}
            if (i==1) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip1").formatted(Formatting.AQUA));}
            if (i==2) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip2").formatted(Formatting.RED));}
            if (i==3) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip3").formatted(Formatting.GOLD));}
            if (i==4) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip4").formatted(Formatting.GREEN));}
            if (i==5) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip5").formatted(Formatting.DARK_PURPLE));}
            if (i==6) {tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip6").formatted(Formatting.YELLOW));}
        }else{
            tooltip.add(Text.translatable("item.dabaosword.arrowrain.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.arrowrain.shift").formatted(Formatting.ITALIC));
        }
    }
    //一次射五发
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !world.isClient) {
            Text a = Text.of("a");
            ArrowEntity arrow1 = new ArrowEntity(world, playerEntity);arrow1.setCustomName(a);
            ArrowEntity arrow2 = new ArrowEntity(world, playerEntity);arrow2.setCustomName(a);
            ArrowEntity arrow3 = new ArrowEntity(world, playerEntity);arrow3.setCustomName(a);
            ArrowEntity arrow4 = new ArrowEntity(world, playerEntity);arrow4.setCustomName(a);
            ArrowEntity arrow5 = new ArrowEntity(world, playerEntity);arrow5.setCustomName(a);
            arrow1.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw()+10, 0.0F, 5.0F, 1.0F);
            arrow2.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw()+5, 0.0F, 5.0F, 1.0F);
            arrow3.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 5.0F, 1.0F);
            arrow4.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw()-5, 0.0F, 5.0F, 1.0F);
            arrow5.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw()-10, 0.0F, 5.0F, 1.0F);
            arrow1.setCritical(true);arrow2.setCritical(true);arrow3.setCritical(true);arrow4.setCritical(true);arrow5.setCritical(true);
            world.spawnEntity(arrow1);world.spawnEntity(arrow2);world.spawnEntity(arrow3);world.spawnEntity(arrow4);world.spawnEntity(arrow5);
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            if (!playerEntity.isCreative()) stack.damage(1, playerEntity,player -> player.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
}
