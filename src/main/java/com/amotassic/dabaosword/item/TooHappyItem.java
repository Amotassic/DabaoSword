package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class TooHappyItem extends CardItem {
    public TooHappyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        if(Screen.hasShiftDown()){
            tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip2"));
        }else{
            tooltip.add(Text.translatable("item.dabaosword.too_happy.tooltip").formatted(Formatting.RED));
            tooltip.add(Text.translatable("dabaosword.shifttooltip"));
        }
    }
    //攻击命中敌人给予其10秒乐不思蜀效果
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld serverWorld && attacker instanceof PlayerEntity player) {
            if (target instanceof PlayerEntity player1 && player1.getInventory().contains(ModItems.WUXIE.getDefaultStack())) {
                for (int i = 0; i < player1.getInventory().size(); i++) {
                    ItemStack wuxie = player1.getInventory().getStack(i);
                    if (wuxie.getItem() == ModItems.WUXIE) {
                        wuxie.decrement(1);
                        target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), Sounds.WUXIE, SoundCategory.PLAYERS, 2.0F, 1.0F);
                        break;
                    }
                }
                if (!player.isCreative()) {stack.decrement(1);}
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.LEBU, SoundCategory.PLAYERS, 2.0F, 1.0F);
            } else {
                target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 10));
                if (!player.isCreative()) stack.decrement(1);
                serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.LEBU, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
