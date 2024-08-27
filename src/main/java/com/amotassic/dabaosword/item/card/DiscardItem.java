package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.ActiveSkillHandler;
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
        if (entity instanceof PlayerEntity target && !user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            if (hasItem(target, ModItems.WUXIE)) {
                voice(target, Sounds.WUXIE);
                removeItem(target, ModItems.WUXIE);
                jizhi(target); benxi(target);
                voice(user, Sounds.GUOHE);
                if (!user.isCreative()) {stack.decrement(1);}
                jizhi(user); benxi(user);
            } else {
                ActiveSkillHandler.openInv(user, target, Text.translatable("dabaosword.discard.title", stack.getName()), stack, ActiveSkillHandler.targetInv(target, true, false, 1));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
