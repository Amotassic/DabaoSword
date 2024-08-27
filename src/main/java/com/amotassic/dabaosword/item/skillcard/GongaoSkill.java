package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
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
                        give(player, new ItemStack(ModItems.GAIN_CARD, 2));
                        setTag(stack, extraHP - 5);
                        voice(player, Sounds.WEIZHONG);
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    public static void gainMaxHp(LivingEntity entity, int hp) {
        Multimap<EntityAttribute, EntityAttributeModifier> maxHP = HashMultimap.create();
        final UUID HP_UUID = UUID.fromString("b29c34f3-1450-48ff-ab28-639647e11862");
        maxHP.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(
                HP_UUID, "Max Hp", hp, EntityAttributeModifier.Operation.ADDITION));
        entity.getAttributes().addTemporaryModifiers(maxHP);
    }
}
