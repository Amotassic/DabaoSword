package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class JiedaoItem extends CardItem{
    public JiedaoItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.jiedao.tooltip"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack stack1 = entity.getMainHandStack();
        if (hand == Hand.MAIN_HAND && !stack1.isEmpty()) {
            if (entity instanceof PlayerEntity player) { PlayerInventory inv = player.getInventory();
                if (inv.contains(ModItems.WUXIE.getDefaultStack())) {
                    int i = inv.getSlotWithStack(ModItems.WUXIE.getDefaultStack());
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.WUXIE, SoundCategory.PLAYERS, 2.0F, 1.0F);
                    user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.JIEDAO, SoundCategory.PLAYERS, 2.0F, 1.0F);
                    if (!user.isCreative()) {stack.decrement(1);}
                    inv.removeStack(i,1);
                } else {
                    user.giveItemStack(stack1.copy()); stack1.setCount(0);
                    user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.JIEDAO, SoundCategory.PLAYERS, 2.0F, 1.0F);
                    if (!user.isCreative()) {stack.decrement(1);}
                }
            } else {
                user.giveItemStack(stack1.copy()); stack1.setCount(0);
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.JIEDAO, SoundCategory.PLAYERS, 2.0F, 1.0F);
                if (!user.isCreative()) {stack.decrement(1);}
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
