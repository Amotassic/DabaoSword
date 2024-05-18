package com.amotassic.dabaosword.effect;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class BingliangEffect extends StatusEffect implements ModTools {
    public BingliangEffect(StatusEffectCategory category, int color) {super(category, color);}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            //清除玩家的牌
            if(amplifier == 1) {
                if (hasItem(player, ModItems.GAIN_CARD)) {
                    removeItem(player, ModItems.GAIN_CARD);
                    //将2级效果换成1级
                    player.removeStatusEffect(ModItems.BINGLIANG);
                    player.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE));
                }
            }
            if (amplifier == 0) {
                if (hasItem(player, ModItems.GAIN_CARD)) {
                    removeItem(player, ModItems.GAIN_CARD);
                    player.removeStatusEffect(ModItems.BINGLIANG);
                }
            }
        }
    }
}
