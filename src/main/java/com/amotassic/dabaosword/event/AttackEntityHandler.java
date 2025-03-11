package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.allTrinkets;
import static com.amotassic.dabaosword.util.ModTools.canTrigger;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!player.isSpectator() && entity instanceof LivingEntity target) {
            if (!(player.getMainHandStack().isOf(ModItems.JUEDOU) || player.getMainHandStack().isOf(ModItems.DISCARD))) {
                if (!world.isClient) {
                    for (var stack : allTrinkets(player)) {
                        if (stack.getItem() instanceof Skill skill && canTrigger(stack, player)) skill.preAttack(stack, target, player);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
