package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EquipmentItem extends TrinketItem {
    public EquipmentItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (stack.getItem() == ModItems.GUDING_WEAPON) {
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.HANBING) {
            tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.QINGGANG) {
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.BAIYIN) {
            tooltip.add(Text.translatable("item.dabaosword.baiyin.tooltip"));
        }

        if (stack.getItem() == ModItems.RATTAN_ARMOR) {
            tooltip.add(Text.translatable("item.dabaosword.rattanarmor.tooltip"));
        }

        if (stack.getItem() == ModItems.CHITU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == ModItems.DILU) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        if (stack.getItem() == ModItems.CARD_PILE) {
            tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.literal(entity.getEntityName()).append(Text.literal("装备了 ").append(stack.getName()))
            ));
        }
        super.onEquip(stack, slot, entity);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            if (stack.getItem() == ModItems.BAIYIN || stack.getItem() == ModItems.RATTAN_ARMOR) gainArmor(entity,5);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            if (stack.getItem() == ModItems.BAIYIN || stack.getItem() == ModItems.RATTAN_ARMOR) gainArmor(entity,0);
        }
    }

    private void gainArmor(LivingEntity entity, int amount) {
        Multimap<EntityAttribute, EntityAttributeModifier> Armor = HashMultimap.create();
        final UUID armor = UUID.fromString("78e52d57-ba65-99a6-a118-686462588db8");
        Armor.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(
                armor, "Extra Armor", amount, EntityAttributeModifier.Operation.ADDITION));
        entity.getAttributes().addTemporaryModifiers(Armor);
    }
}
