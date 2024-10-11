package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.*;

public class GainCardItem extends CardItem {
    public GainCardItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            if (!player.isCreative() && !player.isSpectator() && stack.getItem() == ModItems.GAIN_CARD) {
                draw(player, stack.getCount());
                stack.setCount(0);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            int m;
            //摸牌
            if (user.getMainHandStack().isOf(ModItems.GAIN_CARD)) {
                if (user.isSneaking()) {m=user.getMainHandStack().getCount();} else {m=1;}
                draw(user,m);
                if (!user.isCreative()) {user.getMainHandStack().decrement(m);}
                return TypedActionResult.success(user.getMainHandStack());
            }
            //无中生有
            if (user.getMainHandStack().isOf(ModItems.WUZHONG)) {
                if (cardUsePre(user, user.getMainHandStack(), null)) return TypedActionResult.success(user.getMainHandStack());
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
        if (user instanceof PlayerEntity player) draw(player,2);
        voice(user, Sounds.WUZHONG);
        super.cardUse(user, stack, target);
    }
}
