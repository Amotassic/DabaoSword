package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class WanjianItem extends CardItem.Armoury {
    public WanjianItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (world instanceof ServerLevel sw) {

            Set<LivingEntity> targets = new HashSet<>(sw.players());
            AABB box = new AABB(user.getOnPos()).inflate(10);
            targets.addAll(world.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive));
            var e = ModTools.getClosestEntity(user, LivingEntity.class, 10, l -> !(l instanceof Player));
            if (e != null) e.addTag("wanjian");
            targets.remove(user);

            user.addTag("sha"); //防止触发杀
            onUse(user, user.getItemInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        entity.hurtServer(ModTools.world(user), ModDT.wanjian(user), 6);
        if (entity instanceof Player || entity.entityTags().contains("wanjian")) entity.addEffect(new MobEffectInstance(ModItems.COOLDOWN2, 20, 1, false, false));
    }

    @Override public boolean askForWuxie() {return true;}
}
