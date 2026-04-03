package com.amotassic.dabaosword.item.card;

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

public class ShandianItem extends CardItem.Armoury {
    public ShandianItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (world instanceof ServerLevel sw) {
            ModTools.excuteServerCommand(user, "weather thunder 15s");
            //world.setWeather(0, 15, true, true);

            Set<LivingEntity> targets = new HashSet<>(sw.players());
            AABB box = new AABB(user.getOnPos()).inflate(10);
            targets.addAll(world.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive));
            onUse(user, user.getItemInHand(hand), hand, targets.toArray(new LivingEntity[0]));

            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        if (target != user) ModTools.voice(target, this);
        target.addEffect(new MobEffectInstance(ModItems.SHANDIAN, 299));
    }

    @Override public boolean askForWuxie() {return true;}
}
