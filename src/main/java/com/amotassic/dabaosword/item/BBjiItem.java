package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class BBjiItem extends Item {
    public BBjiItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
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
            voice(user, Sounds.BBJI);
            ItemStack stack = user.getStackInHand(hand);
            stack.damage(1, user, entity -> entity.sendToolBreakStatus(hand));
        }
        return super.use(world, user, hand);
    }
}
