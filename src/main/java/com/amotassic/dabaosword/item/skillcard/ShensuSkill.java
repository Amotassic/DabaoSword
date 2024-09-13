package com.amotassic.dabaosword.item.skillcard;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.noTieji;

public class ShensuSkill extends SkillItem {
    public ShensuSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(player)) {
            double d = Math.min(getEmptySlots(player), 20d) / 40; //当空余20格时，获得最大加成0.5
            gainSpeed(player, Math.max(0, d));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) gainSpeed(entity,0);
    }

    public static void gainSpeed(LivingEntity entity, double value) {
        EntityAttributeModifier modifier = new EntityAttributeModifier(Identifier.of("shensu"), value, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).updateModifier(modifier);
    }

    private int getEmptySlots(PlayerEntity player) {
        int i = 0;
        for (var slot : player.getInventory().main) {if (slot.isEmpty()) i++;}
        return i;
    }
}
