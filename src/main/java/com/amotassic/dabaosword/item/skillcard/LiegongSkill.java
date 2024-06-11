package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

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
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(UUID.fromString("2b3df518-6e44-3554-821b-232333bcef5c"), "Range 13", value, EntityAttributeModifier.Operation.ADDITION);
        Supplier<ImmutableMultimap<EntityAttribute, EntityAttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of(ReachEntityAttributes.REACH, AttributeModifier, ReachEntityAttributes.ATTACK_RANGE, AttributeModifier));

        entity.getAttributes().addTemporaryModifiers(rangeModifier.get());
    }
}
