package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class JiuItem extends CardItem{
    public JiuItem(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.jiu.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.hasStatusEffect(StatusEffects.STRENGTH)) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 15, 1));
            user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.JIU, SoundCategory.PLAYERS, 2.0F, 1.0F);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return super.use(world, user, hand);
    }
}
