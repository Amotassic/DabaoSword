package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class JuedouItem extends CardItem{
    public JuedouItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.dabaosword.juedou.tooltip"));
    }
    //给与攻击和受击双方决斗效果
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
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.JUEDOU, SoundCategory.PLAYERS, 2.0F, 1.0F);
            } else {
                attacker.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 15));
                target.addStatusEffect(new StatusEffectInstance(ModItems.JUEDOUING, 20 * 15));
                if (!player.isCreative()) stack.decrement(1);
                serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.JUEDOU, SoundCategory.PLAYERS, 2.0F, 1.0F);
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
