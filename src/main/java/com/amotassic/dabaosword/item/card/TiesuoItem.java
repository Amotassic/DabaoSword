package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.cardUsePost;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class TiesuoItem extends CardItem {
    public TiesuoItem(Settings settings) {super(settings);}
    //原始的铁索连环
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && !entity.isGlowing() && !(user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) && hand == Hand.MAIN_HAND) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(10));
            for (LivingEntity nearbyEntity : user.getWorld().getEntitiesByClass(LivingEntity.class, box, nearbyEntity -> !nearbyEntity.isGlowing())) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            }
            cardUsePost(user, stack, entity);
            voice(user, Sounds.TIESUO);
            user.removeStatusEffect(StatusEffects.GLOWING);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
    //使用战技时播放纳西妲的语音
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            voice(user, Sounds.NAHIDA, 3);
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
    //看到的就连上
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient && user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(20))
                    .expand(1.0D, 1.0D, 1.0D);
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, nearbyEntity -> !nearbyEntity.isGlowing())) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            if (user instanceof PlayerEntity && !((PlayerEntity) user).isCreative()) {stack.decrement(1);}
        }
        user.removeStatusEffect(StatusEffects.GLOWING);
    }
}
