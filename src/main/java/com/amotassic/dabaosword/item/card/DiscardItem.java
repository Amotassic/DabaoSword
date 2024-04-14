package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.IntStream;

public class DiscardItem extends CardItem implements ModTools {
    public DiscardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.discard.tooltip1"));
        tooltip.add(Text.translatable("item.dabaosword.discard.tooltip2"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity target && hand == Hand.MAIN_HAND && !user.getWorld().isClient) {
            if (hasItem(target, ModItems.WUXIE)) {
                voice(target, Sounds.WUXIE);
                voice(user, Sounds.GUOHE);
                if (!user.isCreative()) {stack.decrement(1);}
                removeItem(target, ModItems.WUXIE);
                return ActionResult.SUCCESS;
            } else {
                DefaultedList<ItemStack> inventory = target.getInventory().main;
                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(i -> inventory.get(i).getItem() instanceof CardItem).boxed().toList();
                if (!cardSlots.isEmpty()) {
                    voice(user, Sounds.GUOHE);
                    int slot = cardSlots.get(((int) (System.currentTimeMillis()/1000) % cardSlots.size()));
                    ItemStack item = inventory.get(slot);
                    item.decrement(1);
                    if (!user.isCreative()) {stack.decrement(1);}
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}
