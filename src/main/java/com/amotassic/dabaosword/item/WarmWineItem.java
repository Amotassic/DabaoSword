package com.amotassic.dabaosword.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public class WarmWineItem extends Item {
    public WarmWineItem(Properties settings) {super(settings);}

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.dabaosword.warm_wine.tooltip").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, EquipmentSlot slot) {
        var nbt = getOrCreateNbt(stack);
        if (nbt.contains("Time")) {
            long time = nbt.getLong("Time").orElse(0L);
            if (world.getGameTime() - time >= 20 * 60 * 3) {
                int count = stack.getCount();
                stack.shrink(count);
                if (entity instanceof LivingEntity living) {
                    give(living, newCard(ModItems.JIU).copyWithCount(count));
                }
            }
        } else {
            nbt.putLong("Time", world.getGameTime());
            setNbt(stack, nbt);
        }
    }

    public static ItemStack dropHead(LivingEntity entity) {
        if (entity instanceof Player player) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(player.getGameProfile()));
            return head;
        }
        if (entity instanceof Creeper) return new ItemStack(Items.CREEPER_HEAD);
        if (entity instanceof Piglin) return new ItemStack(Items.PIGLIN_HEAD);
        if (entity instanceof Skeleton) return new ItemStack(Items.SKELETON_SKULL);
        if (entity instanceof WitherBoss || entity instanceof WitherSkeleton) return new ItemStack(Items.WITHER_SKELETON_SKULL);
        if (entity instanceof Zombie) return new ItemStack(Items.ZOMBIE_HEAD);
        if (entity instanceof EnderDragon) return new ItemStack(Items.DRAGON_HEAD);
        return ItemStack.EMPTY;
    }
}
