package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class JiuItem extends CardItem implements ModTools {
    public JiuItem(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.jiu.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.hasStatusEffect(StatusEffects.STRENGTH) && !world.isClient && hand == Hand.MAIN_HAND) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 15, 1));
            voice(user, Sounds.JIU);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return super.use(world, user, hand);
    }
}
