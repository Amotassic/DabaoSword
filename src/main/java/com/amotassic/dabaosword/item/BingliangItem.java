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

public class BingliangItem extends CardItem{
    public BingliangItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        if(Screen.hasShiftDown()){
            tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip2"));
        }else{
            tooltip.add(Text.translatable("item.dabaosword.bingliang.tooltip").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("dabaosword.shifttooltip"));
        }
    }
    //攻击命中敌人给予其兵粮寸断效果
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld serverWorld && attacker instanceof PlayerEntity player) {
            target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
            if (!player.isCreative()) stack.decrement(1);
            serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.BINGLIANG, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
        return super.postHit(stack, target, attacker);
    }
}
