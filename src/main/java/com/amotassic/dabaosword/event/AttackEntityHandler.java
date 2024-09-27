package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.allTinkets;
import static com.amotassic.dabaosword.util.ModTools.canTrigger;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator() && entity instanceof LivingEntity target) {
            if (!(player.getMainHandStack().isOf(ModItems.JUEDOU) || player.getMainHandStack().isOf(ModItems.DISCARD))) {
                for (var pair : allTinkets(player)) {
                    ItemStack stack = pair.getRight();
                    if (stack.getItem() instanceof SkillItem skill && canTrigger(skill, player)) skill.preAttack(stack, target, player);
                    if (stack.getItem() instanceof Equipment skill) skill.preAttack(stack, target, player);
                }
            }
        }
        return ActionResult.PASS;
    }
}
