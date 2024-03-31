package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Random;

public class TiesuoItem extends CardItem{
    public TiesuoItem(Settings settings) {
        super(settings);
    }
    //动态显示名称，但失败了
    @Override
    public Text getName() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            return Text.translatable("sounds.dabaosword.nahida");
        } else {return super.getName();}
    }
    //原始的铁索连环
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!entity.isGlowing() && !(user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK)) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(10))
                    .expand(0.3D, 0.3D, 0.3D);
            for (LivingEntity nearbyEntity : user.getWorld().getEntitiesByClass(LivingEntity.class, box, nearbyEntity -> !nearbyEntity.isGlowing())) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            }
            if (!user.isCreative()) {stack.decrement(1);}
            user.playSound(Sounds.TIESUO, 2f, 1.0f);
            user.removeStatusEffect(StatusEffects.GLOWING);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
    //使用战技时播放纳西妲的语音
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            float i = new Random().nextFloat();
            if (i < 0.333) {user.playSound(Sounds.NAHIDA1, 2f, 1.0f);}
            else if (0.333<= i && i < 0.666) {user.playSound(Sounds.NAHIDA2, 2f, 1.0f);}
            else {user.playSound(Sounds.NAHIDA3, 2f, 1.0f);}
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
    //看到的就连上
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            Box box = user.getBoundingBox().stretch(user.getRotationVec(1.0F).multiply(20))
                    .expand(1.0D, 1.0D, 1.0D);
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, nearbyEntity -> !nearbyEntity.isGlowing())) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user.getOffHandStack().getItem() == Items.KNOWLEDGE_BOOK) {
            if (user instanceof PlayerEntity && !((PlayerEntity) user).isCreative()) {stack.decrement(1);}
        }
        user.removeStatusEffect(StatusEffects.GLOWING);
    }
}
