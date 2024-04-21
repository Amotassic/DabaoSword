package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class LuoyiSkill extends SkillItem implements ModTools {
    public LuoyiSkill(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack stack2 = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack stack3 = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack stack4 = player.getEquippedStack(EquipmentSlot.FEET);
            boolean noArmor = stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty() && stack4.isEmpty();
            if (noArmor) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1,2,false,true,true));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (new Random().nextFloat() < 0.5) {
            voice(user, Sounds.LUOYI1);
        } else {voice(user, Sounds.LUOYI2);}
        return super.use(world, user, hand);
    }
}
