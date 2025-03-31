package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SunshineSmile extends Item implements Equipment {
    public SunshineSmile() {super(new Item.Settings().maxDamage(999).rarity(Rarity.UNCOMMON));}

    @Override
    public EquipmentSlot getSlotType() {return EquipmentSlot.HEAD;}

    @Override
    public int getEnchantability() {return 25;}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld sw && sw.getTime() % 1200 == 0) {
            if (EnchantmentHelper.getLevel(ModItems.CRIT, stack) == 0) {
                stack.addEnchantment(ModItems.CRIT, 1);
            }
        }
    }
}
