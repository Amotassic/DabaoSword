package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.cardUsePre;

public class NanmanItem extends CardItem {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), null)) return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
        World world = user.getWorld();
        world.getPlayers().forEach(player -> {
            if (player != user) summonRavager(user, player);
        });
        Box box = new Box(user.getBlockPos()).expand(10);
        Predicate<LivingEntity> p = e -> !(e instanceof PlayerEntity) && e != user && e.isAlive() && !e.getCommandTags().contains("b");
        for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, box, p)) {
            summonRavager(user, near);
        }
    }

    private void summonRavager(LivingEntity user, LivingEntity entity) {
        World world = entity.getWorld();
        RavagerEntity ravager = new RavagerEntity(EntityType.RAVAGER, world);
        ravager.setCustomName(Text.of(String.valueOf(user.getId())));
        world.spawnEntity(ravager);
        ravager.setInvulnerable(true);
        ravager.addCommandTag("a"); ravager.addCommandTag("b");
        ravager.refreshPositionAfterTeleport(getBlockInFront(entity, 3));
    }

    public Vec3d getBlockInFront(LivingEntity entity, int distance) {
        Vec3d pos = entity.getPos();
        Vec3d playerDirection = entity.getRotationVec(1.0F);
        double x = pos.x + playerDirection.x * distance;
        double z = pos.z + playerDirection.z * distance;
        return new Vec3d(x, pos.y, z);
    }
}
