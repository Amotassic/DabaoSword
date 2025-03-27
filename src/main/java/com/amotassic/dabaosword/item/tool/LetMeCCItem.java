package com.amotassic.dabaosword.item.tool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.command.InfoCommand.openFullInv;
import static com.amotassic.dabaosword.util.ModTools.getClosestEntity;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class LetMeCCItem extends Item {
    public LetMeCCItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.dabaosword.let_me_cc.tooltip"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            voice(user, this, 1);
            openFullInv(user, entity, true);
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (!user.isSneaking()) {
                LivingEntity closest = getClosestEntity(user, LivingEntity.class, 10, LivingEntity::isAlive);
                if (closest != null) {
                    voice(user, this, 1);
                    openFullInv(user, closest, true);
                    return ActionResult.SUCCESS_SERVER;
                }
            } else {
                voice(user, this, 1);
                openFullInv(user, user, true);
                return ActionResult.SUCCESS_SERVER;
            }
        }
        return super.use(world, user, hand);
    }
}
