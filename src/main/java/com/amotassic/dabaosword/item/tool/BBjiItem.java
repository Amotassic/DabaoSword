package com.amotassic.dabaosword.item.tool;

import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.voice;
import static com.amotassic.dabaosword.util.ModTools.world;

public class BBjiItem extends Item {
    public BBjiItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.dabaosword.bbji.tooltip"));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            Box box = new Box(user.getBlockPos()).expand(13);
            for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != user)) {
                nearbyEntity.timeUntilRegen = 0;
                nearbyEntity.damage(world(user), ModDT.bbll(user),2);
            }
            voice(user, this);
            ItemStack stack = user.getStackInHand(hand);
            EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.damage(1, user, slot);
        }
        return super.use(world, user, hand);
    }
}
