package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class LuoyiSkill extends SkillItem {
    public LuoyiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) gainStrength(entity, getEmptyArmorSlot(entity) + 1);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) gainStrength(entity,0);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && !user.isSneaking()) voice(user, Sounds.LUOYI);
        return super.use(world, user, hand);
    }

    private void gainStrength(LivingEntity entity, int value) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("attack_damage"), value, EntityAttributeModifier.Operation.ADD_VALUE);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).updateModifier(AttributeModifier);
    }

    private int getEmptyArmorSlot(LivingEntity entity) {
        int i = 0;
        for (var slot : entity.getArmorItems()) {if (slot.isEmpty()) i++;}
        return i;
    }
}
