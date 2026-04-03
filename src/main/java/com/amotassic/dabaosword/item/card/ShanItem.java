package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class ShanItem extends CardItem.Basic {
    public ShanItem(Properties settings) {super(settings);}

    //使用后，向前冲刺一段距离，无敌0.5秒，冷却时间1秒
    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        //判断是否有独立冷却buff，若冷却中则无法生效
        if (!world.isClientSide() && !user.hasEffect(ModItems.COOLDOWN2)) {
            onUse(user, user.getItemInHand(hand), hand, user);
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.use(world, user, hand);
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        Vec3 momentum = user.getForward().scale(3);
        user.hurtMarked = true; user.setDeltaMovement(momentum.x, 0, momentum.z);
        user.addEffect(new MobEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        user.addEffect(new MobEffectInstance(ModItems.COOLDOWN2, 20,0,false,false,false));
    }
}
