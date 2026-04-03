package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, Level world, @NonNull InteractionHand hand, @NonNull Entity entity, EntityHitResult hitResult) {
        if (!world.isClientSide() && !player.isSpectator()) {
            //决斗等物品虽然手长，但过远时普通伤害无效
            if (ModTools.shouldReachLong(player) && entity.distanceTo(player) >= 5) return InteractionResult.FAIL;
            if (entity instanceof LivingEntity target) for (var skill : ModTools.getSkillsMayUse(player)) {
                skill.item.preAttack(player, target, skill);
            }
        }
        return InteractionResult.PASS;
    }
}
