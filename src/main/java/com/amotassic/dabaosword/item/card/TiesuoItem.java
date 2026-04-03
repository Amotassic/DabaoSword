package com.amotassic.dabaosword.item.card;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class TiesuoItem extends CardItem.Armoury {
    public TiesuoItem(Properties settings) {super(settings);}

    //原始的铁索连环
    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && !entity.isCurrentlyGlowing() && !user.getOffhandItem().is(Items.KNOWLEDGE_BOOK)) {
            AABB box = new AABB(entity.getOnPos()).inflate(5);
            Set<LivingEntity> targets = new HashSet<>(user.level().getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive));
            targets.remove(user);
            onUse(user, user.getItemInHand(hand), hand, targets.toArray(new LivingEntity[0]));
            user.removeEffect(MobEffects.GLOWING);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, true,false));
    }

    //使用战技时播放纳西妲的语音
    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (!world.isClientSide() && user.getOffhandItem().is(Items.KNOWLEDGE_BOOK)) {
            voice(user, "nahida", 3);
        }
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {return 1200;}
    //看到的就连上
    @Override
    public void onUseTick(Level world, @NonNull LivingEntity user, @NonNull ItemStack stack, int ticksRemaining) {
        if (!world.isClientSide() && user.getOffhandItem().is(Items.KNOWLEDGE_BOOK)) {
            AABB box = user.getBoundingBox().expandTowards(user.getViewVector(1.0F).scale(20))
                    .inflate(1.0D, 1.0D, 1.0D);
            for (var entity : world.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != user)) {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false,false));
            }
        }
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, Level world, @NonNull LivingEntity user, int remainingTime) {
        if (!world.isClientSide() && user.getOffhandItem().is(Items.KNOWLEDGE_BOOK)) {
            if (user instanceof Player player && !player.isCreative()) stack.shrink(1);
        }
        return false;
    }

    @Override public boolean rangedUse() {return true;}

    @Override public boolean askForWuxie() {return true;}
}
