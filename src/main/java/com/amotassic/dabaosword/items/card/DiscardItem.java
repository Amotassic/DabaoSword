package com.amotassic.dabaosword.items.card;

import com.amotassic.dabaosword.Sounds;
import com.amotassic.dabaosword.items.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.IntStream;

public class DiscardItem extends CardItem{
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
        if (entity instanceof PlayerEntity target && hand == Hand.MAIN_HAND) {
            PlayerInventory inv = target.getInventory();
            if (inv.contains(ModItems.WUXIE.getDefaultStack())) {
                int i = inv.getSlotWithStack(ModItems.WUXIE.getDefaultStack());
                target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), Sounds.WUXIE, SoundCategory.PLAYERS, 2.0F, 1.0F);
                if (!user.isCreative()) {stack.decrement(1);}
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.GUOHE, SoundCategory.PLAYERS, 2.0F, 1.0F);
                inv.removeStack(i,1);
                return ActionResult.SUCCESS;
            } else {
                DefaultedList<ItemStack> inventory = target.getInventory().main;
                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(i -> inventory.get(i).getItem() instanceof CardItem).boxed().toList();
                if (!cardSlots.isEmpty()) {
                    user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.GUOHE, SoundCategory.PLAYERS, 2.0F, 1.0F);
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
