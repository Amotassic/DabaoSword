package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AttackEntityHandler implements AttackEntityCallback {
    //监听事件：若玩家杀死敌对生物，有概率摸牌；杀死玩家可以摸两张牌
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator()) {
            if (entity instanceof HostileEntity) {
                if (new Random().nextFloat() < 0.1) player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));
            }
            if (entity instanceof PlayerEntity) {
                if (new Random().nextFloat() < 0.1) player.giveItemStack(new ItemStack(ModItems.WUZHONG));
            }
        }
        if (!world.isClient && entity instanceof LivingEntity target && target.hasStatusEffect(StatusEffects.GLOWING)) {
            Box box = new Box(entity.getBlockPos()).expand(20); // 检测范围，根据需要修改
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, nearbyEntity -> nearbyEntity.hasStatusEffect(StatusEffects.GLOWING))) {
                nearbyEntity.damage(player.getWorld().getDamageSources().playerAttack(player), 10);
                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING);
            }
        }
        return ActionResult.PASS;
    }
}
