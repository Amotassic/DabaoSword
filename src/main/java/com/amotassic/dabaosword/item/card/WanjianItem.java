package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class WanjianItem extends CardItem.Armoury {
    public WanjianItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld sw && hand == Hand.MAIN_HAND) {

            Set<LivingEntity> targets = new HashSet<>(sw.getPlayers());
            Box box = new Box(user.getBlockPos()).expand(10);
            targets.addAll(world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive));
            var e = ModTools.getClosestEntity(user, LivingEntity.class, 10, l -> !(l instanceof PlayerEntity));
            if (e != null) e.addCommandTag("wanjian");
            targets.remove(user);

            user.addCommandTag("sha"); //防止触发杀
            onUse(user, user.getMainHandStack(), targets.toArray(new LivingEntity[0]));
            return ActionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        entity.damage(ModTools.world(user), ModDT.wanjian(user), 6);
        if (entity instanceof PlayerEntity || entity.getCommandTags().contains("wanjian")) entity.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 20, 1, false, false));
    }

    @Override public boolean askForWuxie() {return true;}
}
