package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.*;

public class ShanItem extends CardItem {
    public ShanItem(Settings settings) {super(settings);}
    //使用后，向前冲刺一段距离，无敌0.5秒，冷却时间1秒
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //判断是否有独立冷却buff，若冷却中则无法生效
        if (!world.isClient && !user.hasStatusEffect(ModItems.COOLDOWN2) && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), null)) return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
        Vec3d momentum = user.getRotationVector().multiply(3);
        user.velocityModified = true; user.addVelocity(momentum.getX(),0 ,momentum.getZ());
        int i = hasTrinket(SkillCards.LEIJI, user) ? 3 : 0;
        user.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 20,i,false,false,false));
        voice(user, Sounds.SHAN);
        super.cardUse(user, stack, target);
    }
}
