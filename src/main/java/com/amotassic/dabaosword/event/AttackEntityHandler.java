package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ShensuPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.amotassic.dabaosword.util.ModTools.*;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!player.isSpectator() && entity instanceof LivingEntity target) {
            if (!(player.getMainHandStack().isOf(ModItems.JUEDOU) || player.getMainHandStack().isOf(ModItems.DISCARD))) {
                if (world.isClient) {
                    if (hasTrinket(SkillCards.SHENSU, player)) {
                        Vec3d lastPos = new Vec3d(player.lastRenderX, player.lastRenderY, player.lastRenderZ);
                        float speed = (float) (player.getPos().distanceTo(lastPos) * 20);
                        ClientPlayNetworking.send(new ShensuPayload(speed));
                    }
                } else {
                    for (var stack : allTrinkets(player)) {
                        if (stack.getItem() instanceof Skill skill && canTrigger(stack, player)) skill.preAttack(stack, target, player);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
