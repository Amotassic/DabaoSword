package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.item.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EquipmentItem extends TrinketItem {
    public EquipmentItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (stack.getItem() == ModItems.GUDING_WEAPON) {
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.FANGTIAN) {
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.HANBING) {
            tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.QINGGANG) {
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.QINGLONG) {
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.BAGUA) {
            tooltip.add(Text.translatable("item.dabaosword.bagua.tooltip"));
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
            if (armorTrinket(stack)) gainArmor(entity,5);
            if (stack.getItem() != ModItems.CARD_PILE && !EnchantmentHelper.hasBindingCurse(stack)) {//给装备上绑定诅咒
                stack.addEnchantment(Enchantments.BINDING_CURSE, 1);
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            if (armorTrinket(stack)) gainArmor(entity,0);
        }
    }

    private boolean armorTrinket(ItemStack stack) {
        return stack.getItem() == ModItems.BAIYIN || stack.getItem() == ModItems.RATTAN_ARMOR || stack.getItem() == ModItems.BAGUA;
    }

    private void gainArmor(LivingEntity entity, int amount) {
        Multimap<EntityAttribute, EntityAttributeModifier> Armor = HashMultimap.create();
        final UUID armor = UUID.fromString("78e52d57-ba65-99a6-a118-686462588db8");
        Armor.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(
                armor, "Extra Armor", amount, EntityAttributeModifier.Operation.ADDITION));
        entity.getAttributes().addTemporaryModifiers(Armor);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (equipItem(user, stack)) return TypedActionResult.success(stack, world.isClient);
        if (replaceEquip(user, stack)) return TypedActionResult.success(stack, world.isClient);
        return TypedActionResult.pass(stack);
    }

    private boolean replaceEquip(PlayerEntity player, ItemStack stack) {
        var optional = TrinketsApi.getTrinketComponent(player);
        Map<Integer, Map<String, TrinketInventory>> map = replaceSlot(player, stack);
        if (!map.isEmpty() && stack.getItem() != ModItems.CARD_PILE && optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                if (map.values().stream().findFirst().get().equals(group)) {//如果饰品组相同，则继续下一步
                    for (TrinketInventory inv : group.values()) {
                        List<Integer> slots = map.keySet().stream().toList();
                        if (!slots.isEmpty()) {
                            Random r = new Random(); int index = r.nextInt(slots.size()); int i = slots.get(index);
                            ItemStack newStack = stack.copy();
                            inv.setStack(i, newStack); stack.setCount(0);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Map<Integer, Map<String, TrinketInventory>> replaceSlot(PlayerEntity player, ItemStack stack) {
        Map<Integer, Map<String, TrinketInventory>> m = new HashMap<>();
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        //如果对应装备栏的物品与待装备的物品有完全相同的标签，则添加到map中
                        if (!inv.getStack(i).isEmpty() && inv.getStack(i).streamTags().toList().equals(stack.streamTags().toList())) {
                            m.put(i, group);
                        }
                    }
                }
            }
        }
        return m;
    }
}
