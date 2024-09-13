package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.*;

public class GongaoSkill extends SkillItem {
    public GongaoSkill(Settings settings) {super(settings);}

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) gainMaxHp(entity, 0);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            int extraHP = getTag(stack);

            gainMaxHp(entity, extraHP);
            if (entity.getWorld().getTime() % 600 == 0) { // 每30s触发扣体力上限
                if (entity instanceof PlayerEntity player) {
                    if (extraHP >= 5 && !player.isCreative() && !player.isSpectator()) {
                        draw(player, 2);
                        stack.set(ModItems.TAGS, extraHP - 5);
                        voice(player, Sounds.WEIZHONG);
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    private void gainMaxHp(LivingEntity entity, int value) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("max_hp"), value, EntityAttributeModifier.Operation.ADD_VALUE);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH)).updateModifier(AttributeModifier);
    }
}
