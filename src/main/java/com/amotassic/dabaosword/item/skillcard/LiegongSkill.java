package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.UUID;

public class LiegongSkill extends SkillItem implements ModTools {
    public LiegongSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && noTieji(entity)) {
            if (!entity.hasStatusEffect(ModItems.COOLDOWN)) gainReach(entity,13);
            else gainReach(entity,0);
        }
        super.tick(stack, slot, entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient){
            gainReach(entity,0);
        }
    }

    private void gainReach(LivingEntity entity, int value) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("range_13"), value, EntityAttributeModifier.Operation.ADD_VALUE);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)).updateModifier(AttributeModifier);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)).updateModifier(AttributeModifier);
    }
}
