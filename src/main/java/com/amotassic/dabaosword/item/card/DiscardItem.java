package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

public class DiscardItem extends CardItem {
    public DiscardItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && entity instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,114,false,false,false));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        if (user instanceof PlayerEntity player) {
            if (entity instanceof PlayerEntity target) {
                openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, targetInv(target, true, false, 1));
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                if (isCard(entity.getMainHandStack())) stacks.add(entity.getMainHandStack());
                if (isCard(entity.getOffHandStack())) stacks.add(entity.getOffHandStack());
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    voice(player, Sounds.GUOHE);
                    cardDiscard(entity, chosen, 1, false);
                    nonPreUseCardDecrement(player, stack, entity);
                }
            }
        } else {
            if (entity instanceof PlayerEntity player) { //如果是玩家则弃牌
                List<ItemStack> candidate = new ArrayList<>(new CardPileInventory(player).nonEmpty);
                //把背包中的卡牌添加到待选物品中
                DefaultedList<ItemStack> inventory = player.getInventory().main;
                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(j -> isCard(inventory.get(j))).boxed().toList();
                for (Integer slot : cardSlots) {
                    candidate.add(inventory.get(slot));
                }
                //把饰品栏的卡牌添加到待选物品中
                int equip = 0; //用于标记装备区牌的数量
                for (var entry : allTrinkets(player)) {
                    ItemStack stack1 = entry.getRight();
                    if (isCard(stack1)) candidate.add(stack1);
                    equip++;
                }
                if (!candidate.isEmpty()) {
                    int index = new Random().nextInt(candidate.size());
                    ItemStack chosen = candidate.get(index);
                    player.sendMessage(Text.literal(user.getEntityName()).append(Text.translatable("dabaosword.discard")).append(chosen.toHoverableText()));
                    cardDiscard(player, chosen, 1, index > candidate.size() - equip);
                    voice(user, Sounds.GUOHE);
                    nonPreUseCardDecrement(user, stack, entity);
                }
            } else { //如果不是玩家则随机弃置它的主副手物品和装备
                List<ItemStack> candidate = new ArrayList<>();
                if (!entity.getMainHandStack().isEmpty()) candidate.add(entity.getMainHandStack());
                if (!entity.getOffHandStack().isEmpty()) candidate.add(entity.getOffHandStack());
                for (ItemStack armor : entity.getArmorItems()) {
                    if (!armor.isEmpty()) candidate.add(armor);
                }
                if (!candidate.isEmpty()) {
                    int index = new Random().nextInt(candidate.size());
                    ItemStack chosen = candidate.get(index);
                    if (isCard(chosen)) cardDiscard(entity, chosen, 1, false);
                    voice(user, Sounds.GUOHE);
                    nonPreUseCardDecrement(user, stack, entity);
                }
            }
        }
    }
}
