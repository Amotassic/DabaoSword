package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.item.card.ArrowRainItem.arrowRain;

public class WanjianItem extends CardItem implements ModTools {
    private int lastTime;

    public WanjianItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            lastTime = 50;
            voice(user, Sounds.WANJIAN);
            jizhi(user);
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity user) {
            if (lastTime > 0) {
                lastTime--;
                arrowRain(user);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
