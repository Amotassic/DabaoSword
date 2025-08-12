package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ShandianItem extends CardItem.Armoury {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld sw) {
            ModTools.excuteServerCommand(user, "weather thunder 15s");
            //world.setWeather(0, 15, true, true);

            Set<LivingEntity> targets = new HashSet<>(sw.getPlayers());
            Box box = new Box(user.getBlockPos()).expand(10);
            targets.addAll(world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive));
            onUse(user, user.getStackInHand(hand), hand, targets.toArray(new LivingEntity[0]));

            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        if (target != user) ModTools.voice(target, this);
        target.addStatusEffect(new StatusEffectInstance(ModItems.SHANDIAN, 299));
    }

    @Override public boolean askForWuxie() {return true;}
}
