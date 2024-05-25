package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class LuoshenSkill extends ActiveSkill implements ModTools {
    public LuoshenSkill(Settings settings) {super(settings);}

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; stack.set(ModItems.CD, cd);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
