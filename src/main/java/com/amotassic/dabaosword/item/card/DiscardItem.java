package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
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
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class DiscardItem extends CardItem {
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
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (entity instanceof PlayerEntity target) {
                if (hasItem(target, ModItems.WUXIE)) {
                    voice(target, Sounds.WUXIE);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(target, getItem(target, ModItems.WUXIE), null);
                    voice(user, Sounds.GUOHE);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, entity);
                } else {
                    openInv(user, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, targetInv(target, true, false, 1));
                }
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                if (isCard(entity.getMainHandStack())) stacks.add(entity.getMainHandStack());
                if (isCard(entity.getOffHandStack())) stacks.add(entity.getOffHandStack());
                if (!stacks.isEmpty()) {
                    ItemStack chosen = stacks.get(new Random().nextInt(stacks.size()));
                    voice(user, Sounds.GUOHE);
                    chosen.decrement(1);
                    CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, entity);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
