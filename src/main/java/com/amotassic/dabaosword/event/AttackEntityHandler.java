package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!world.isClient && !player.isSpectator() && entity instanceof LivingEntity target) {
            if (ModTools.shouldReachLong(player) && target.distanceTo(player) >= 5) return ActionResult.PASS;
            for (var skill : ModTools.getSkillsMayUse(player)) {
                skill.item.preAttack(player, target, skill);
            }
        }
        return ActionResult.PASS;
    }
}
