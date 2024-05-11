package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class DiscardItem extends CardItem implements ModTools {
    public DiscardItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && entity instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,114,false,false,false));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target && !user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (hasItem(target, ModItems.WUXIE)) {
                voice(target, Sounds.WUXIE);
                removeItem(target, ModItems.WUXIE);
                jizhi(target); benxi(target);
                voice(user, Sounds.GUOHE);
                if (!user.isCreative()) {stack.decrement(1);}
                jizhi(user); benxi(user);
                return ActionResult.SUCCESS;
            } else {
                List<ItemStack> candidate = new ArrayList<>();
                //把背包中的卡牌添加到待选物品中
                DefaultedList<ItemStack> inventory = target.getInventory().main;
                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(
                                i -> inventory.get(i).isIn(Tags.Items.CARD) || inventory.get(i).getItem() == ModItems.GAIN_CARD)
                        .boxed().toList();
                for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
                //把饰品栏的卡牌添加到待选物品中
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
                if(component.isPresent()) {
                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                        ItemStack stack1 = entry.getRight();
                        if(stack1.isIn(Tags.Items.CARD)) {candidate.add(stack1);}
                    }
                }

                if(!candidate.isEmpty()) {
                    Random r = new Random();
                    int index = r.nextInt(candidate.size());
                    ItemStack chosen = candidate.get(index);
                    voice(user, Sounds.GUOHE);
                    if (!user.isCreative()) {stack.decrement(1);}
                    jizhi(user); benxi(user);
                    target.sendMessage(Text.literal(user.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(chosen.getName()));
                    chosen.decrement(1);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}
