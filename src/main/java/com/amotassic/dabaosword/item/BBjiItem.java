package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BBjiItem extends Item implements ModTools {
    public BBjiItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.bbji.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            Box box = new Box(user.getBlockPos()).expand(13);
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != user)) {
                nearbyEntity.timeUntilRegen = 0;
                nearbyEntity.damage(user.getDamageSources().sonicBoom(user),2);
            }
            float i = new Random().nextFloat();
            if (i < (float) 1/6) {voice(user, Sounds.CHENGLVE1);
            } else if (i < (float) 1/3) {voice(user, Sounds.CHENGLVE2);
            } else if (i < 0.5) {voice(user, Sounds.CUNMU1);
            } else if (i < (float) 2/3) {voice(user, Sounds.CUNMU2);
            } else if (i < (float) 5/6) {voice(user, Sounds.SHICAI1);
            } else {voice(user, Sounds.SHICAI2);}
            ItemStack stack = user.getStackInHand(hand);
            EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.damage(1, user, slot);
        }
        return super.use(world, user, hand);
    }
}
