package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amotassic.dabaosword.command.InfoCommand.openFullInv;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class LetMeCCItem extends Item {
    public LetMeCCItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.let_me_cc.tooltip"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            voice(user, Sounds.LET_ME_CC);
            openFullInv(user, entity, true);
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (!user.isSneaking()) {
                LivingEntity closest = getClosestEntity(user, 10);
                if (closest != null) {
                    voice(user, Sounds.LET_ME_CC);
                    openFullInv(user, closest, true);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
            } else {
                voice(user, Sounds.LET_ME_CC);
                openFullInv(user, user, true);
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return super.use(world, user, hand);
    }

    public static @Nullable LivingEntity getClosestEntity(Entity entity, double boxLength) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box box = new Box(entity.getBlockPos()).expand(boxLength);
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity1 -> entity1 != entity);
            if (!entities.isEmpty()) {
                Map<Float, LivingEntity> map = new HashMap<>();
                for (var e : entities) {
                    map.put(e.distanceTo(entity), e);
                }
                float min = Collections.min(map.keySet());
                return map.values().stream().toList().get(map.keySet().stream().toList().indexOf(min));
            }
        }
        return null;
    }
}
