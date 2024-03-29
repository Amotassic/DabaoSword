package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class BingliangEffect extends StatusEffect {
    public BingliangEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //添加一个持续的虚弱
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 2,0,false,false,false));
        if (entity instanceof PlayerEntity player) {
            //遍历玩家背包，清除玩家的牌
            if(amplifier == 1) {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == ModItems.GAIN_CARD) {
                        stack.decrement(1);
                        //将2级效果换成1级
                        player.removeStatusEffect(ModItems.BINGLIANG);
                        player.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,0));
                        break;
                    }
                }
            }
            if (amplifier == 0) {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == ModItems.GAIN_CARD) {
                        stack.decrement(1);
                        player.removeStatusEffect(ModItems.BINGLIANG);
                        break;
                    }
                }
            }
        }
    }
}
