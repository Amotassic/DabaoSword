package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.world;

public class NanmanItem extends CardItem.Armoury {
    public NanmanItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld sw) {

            Set<LivingEntity> targets = new HashSet<>(sw.getPlayers());
            Box box = new Box(user.getBlockPos()).expand(10);
            Predicate<LivingEntity> p = e -> e.isAlive() && !e.getCommandTags().contains("b");
            targets.addAll(world.getEntitiesByClass(LivingEntity.class, box, p));
            targets.remove(user);

            user.addCommandTag("nanman"); //防止触发杀
            onUse(user, user.getStackInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        //防止触发闪
        entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 2, 0, false, false));
        entity.damage(world(user), ModDT.nanman(user), 6);
        summonRavager(entity);
    }

    private void summonRavager(LivingEntity entity) {
        World world = entity.getWorld();
        RavagerEntity ravager = new RavagerEntity(EntityType.RAVAGER, world);
        ravager.setCustomName(Text.of(String.valueOf(entity.getId())));
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

    @Override public boolean askForWuxie() {return true;}
}
