package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ShanItem extends CardItem implements ModTools {

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext){
        tooltip.add(Text.translatable("item.dabaosword.shan.tooltip"));
    }
    public ShanItem(Settings settings) {
        super(settings);
    }
    //使用后，向前冲刺一段距离，无敌0.5秒，冷却时间1秒
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //判断是否有独立冷却buff，若冷却中则无法生效
        if (!user.hasStatusEffect(ModItems.COOLDOWN2) && hand == Hand.MAIN_HAND) {
            Vec3d momentum = user.getRotationVector().multiply(3);
            user.addVelocity(momentum.getX(),0 ,momentum.getZ());
            user.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
            user.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 20,0,false,false,false));
            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
            voice(user, Sounds.SHAN);
        }
        return super.use(world, user, hand);
    }
}
