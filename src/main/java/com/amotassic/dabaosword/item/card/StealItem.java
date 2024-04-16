package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.stream.IntStream;

public class StealItem extends CardItem implements ModTools {
    public StealItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target && hand == Hand.MAIN_HAND && !user.getWorld().isClient) {
            if (hasItem(target, ModItems.WUXIE)) {
                voice(target, Sounds.WUXIE);
                voice(user, Sounds.SHUNSHOU);
                if (!user.isCreative()) {stack.decrement(1);}
                removeItem(target, ModItems.WUXIE);
                return ActionResult.SUCCESS;
            } else {
                DefaultedList<ItemStack> inventory = target.getInventory().main;
                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(i -> inventory.get(i).getItem() instanceof CardItem).boxed().toList();
                if (!cardSlots.isEmpty()) {
                    voice(user, Sounds.SHUNSHOU);
                    int slot = cardSlots.get(((int) (System.currentTimeMillis()/1000) % cardSlots.size()));
                    ItemStack item = inventory.get(slot);
                    if (!user.isCreative()) {stack.decrement(1);}
                    user.giveItemStack(item.copyWithCount(1)); /*顺手：复制一个物品*/ item.decrement(1);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}
