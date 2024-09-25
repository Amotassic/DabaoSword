package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
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

import java.util.Optional;

public class AttackEntityHandler implements AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator() && entity instanceof LivingEntity target) {
            if (!(player.getMainHandStack().isOf(ModItems.JUEDOU) || player.getMainHandStack().isOf(ModItems.DISCARD))) {
                Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
                if(optionalComponent.isEmpty()) return ActionResult.PASS;

                TrinketComponent component = optionalComponent.get();
                for (var pair : component.getAllEquipped()) {
                    ItemStack stack = pair.getRight();
                    if (stack.getItem() instanceof SkillItem skill) skill.preAttack(stack, target, player);
                    if (stack.getItem() instanceof Equipment skill) skill.preAttack(stack, target, player);
                }
            }
        }
        return ActionResult.PASS;
    }
}
