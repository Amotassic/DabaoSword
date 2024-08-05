package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.ModItems;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import static com.amotassic.dabaosword.util.ModTools.getCD;

public class FangtianWeapon extends EquipmentItem {
    public FangtianWeapon(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int cd = getCD(stack);
            if (cd > 0 && world.getTime() % 20 == 0) cd--; stack.set(ModItems.CD, cd);
        }
        super.tick(stack, slot, entity);
    }
}
