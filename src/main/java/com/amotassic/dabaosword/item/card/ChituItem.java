package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChituItem extends CardItem implements ModTools {
    public ChituItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (hasItem(player, SkillCards.MASHU)) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,2));
            } else {player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1));}
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
