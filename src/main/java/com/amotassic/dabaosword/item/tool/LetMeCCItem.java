package com.amotassic.dabaosword.item.tool;

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
import java.util.function.Predicate;

import static com.amotassic.dabaosword.command.InfoCommand.openFullInv;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class LetMeCCItem extends Item {
    public LetMeCCItem() {super(new Item.Settings().maxCount(1));}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.let_me_cc.tooltip"));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND) {
            voice(user, Sounds.LET_ME_CC, 1);
            openFullInv(user, entity, true);
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (!user.isSneaking()) {
                LivingEntity closest = getClosestEntity(user, LivingEntity.class, 10, entity -> entity != user);
                if (closest != null) {
                    voice(user, Sounds.LET_ME_CC, 1);
                    openFullInv(user, closest, true);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
            } else {
                voice(user, Sounds.LET_ME_CC, 1);
                openFullInv(user, user, true);
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return super.use(world, user, hand);
    }

    public static @Nullable <T extends Entity> T getClosestEntity(Entity entity, Class<T> clazz, double boxLength, Predicate<T> predicate) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box box = new Box(entity.getBlockPos()).expand(boxLength);
            List<T> entities = world.getEntitiesByClass(clazz, box, predicate);
            if (!entities.isEmpty()) {
                Map<Float, T> map = new HashMap<>();
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
