package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.benxi;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class PeachItem extends CardItem {
    public PeachItem(Settings settings) {super(settings);}

    //非潜行时右键，给自己回血
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient && player.getHealth() < player.getMaxHealth() && !player.isSneaking() && hand == Hand.MAIN_HAND) {
            player.heal(5);
            voice(player, Sounds.RECOVER); benxi(player);
            if (!player.isCreative()) {stack.decrement(1);}
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
    //潜行时对生物右键，给其他生物回血
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack stack1 = user.getStackInHand(hand);
        if (!user.getWorld().isClient && user.isSneaking() && hand == Hand.MAIN_HAND) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(5); benxi(user);
                entity.playSound(Sounds.RECOVER,1.0F,1.0F);
                if (!user.isCreative()) {stack1.decrement(1);}
                return ActionResult.success(!user.getWorld().isClient);
            }
        }
        return ActionResult.PASS;
    }
}
