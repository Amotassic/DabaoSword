package com.amotassic.dabaosword.item.skillcard;

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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

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

    public static void gainStrength(LivingEntity entity, int value) {
        Multimap<EntityAttribute, EntityAttributeModifier> attack = HashMultimap.create();
        final UUID A_UUID = UUID.fromString("b29c34f3-1450-48ff-ab28-639647e11864");
        attack.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                A_UUID, "Attack Damage", value, EntityAttributeModifier.Operation.ADDITION));
        entity.getAttributes().addTemporaryModifiers(attack);
    }

    private int getEmptyArmorSlot(LivingEntity entity) {
        int i = 0;
        for (var slot : entity.getArmorItems()) {if (slot.isEmpty()) i++;}
        return i;
    }
}
