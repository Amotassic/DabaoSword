package com.amotassic.dabaosword.item.equipment;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import static com.amotassic.dabaosword.util.ModTools.getCD;
import static com.amotassic.dabaosword.util.ModTools.setCD;

public class FangtianWeapon extends EquipmentItem {
    public FangtianWeapon(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            int cd = getCD(stack);
            if (cd > 0 && world.getTime() % 20 == 0) cd--; setCD(stack, cd);
        }
        super.tick(stack, slot, entity);
    }
}
