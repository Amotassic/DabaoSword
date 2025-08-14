package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class TiesuoItem extends CardItem.Armoury {
    //原始的铁索连环
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && !entity.isGlowing() && !user.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(10));
            Set<LivingEntity> targets = new HashSet<>(user.getWorld().getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive));
            onUse(user, user.getStackInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            user.removeStatusEffect(StatusEffects.GLOWING);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, -1, 0, false, true,false));
    }

    //使用战技时播放纳西妲的语音
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) {
            voice(user, "nahida", 3);
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) {return 1200;}
    //看到的就连上
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient && user.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(20))
                    .expand(1.0D, 1.0D, 1.0D);
            for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive)) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, -1, 0, false, false,false));
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user.getOffHandStack().isOf(Items.KNOWLEDGE_BOOK)) {
            if (user instanceof PlayerEntity player && !player.isCreative()) {stack.decrement(1);}
        }
        user.removeStatusEffect(StatusEffects.GLOWING);
    }

    @Override public boolean askForWuxie() {return true;}
}
