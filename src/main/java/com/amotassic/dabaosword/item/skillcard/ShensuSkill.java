package com.amotassic.dabaosword.item.skillcard;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

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
        Multimap<EntityAttribute, EntityAttributeModifier> modifier = HashMultimap.create();
        final UUID uuid = UUID.fromString("7a0a818f-4487-4a98-91c4-14e75d6c1d7d");
        modifier.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                uuid, "shensu", value, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        entity.getAttributes().addTemporaryModifiers(modifier);
    }

    private int getEmptySlots(PlayerEntity player) {
        int i = 0;
        for (var slot : player.getInventory().main) {if (slot.isEmpty()) i++;}
        return i;
    }
}
