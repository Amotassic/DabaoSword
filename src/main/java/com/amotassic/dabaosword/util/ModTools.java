package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;

public interface ModTools {
    //判断是否有某个饰品
    default boolean hasTrinket(Item item, PlayerEntity player) {
        return trinketItem(item, player) != null;
    }

    default ItemStack trinketItem(Item item, PlayerEntity player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return null;

        TrinketComponent component = optionalComponent.get();
        return component.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(null);
    }

    //判断玩家是否有某个物品
    default boolean hasItem(@NotNull PlayerEntity player, @NotNull Item item) {
        return player.getInventory().contains(item.getDefaultStack());
    }
    //移除玩家的1个物品
    default void removeItem(@NotNull PlayerEntity player, @NotNull Item item) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        inv.removeStack(i, 1);
    }

    default ItemStack stackWith(Item item, PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        return inv.getStack(i);
    }

    default Boolean hasItemInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        return player.getInventory().contains(tag);
    }

    default int getSlotInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(tag)) continue;
            return i;
        }
        return -1;
    }

    default ItemStack stackInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = getSlotInTag(tag, player);
        return inv.getStack(i);
    }

    default int getShaSlot(@NotNull PlayerEntity player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(Tags.Items.SHA)) continue;
            return i;
        }
        return -1;
    }
    //只检测玩家物品栏前18格是否有杀
    default ItemStack shaStack(@NotNull PlayerEntity player) {
        return player.getInventory().getStack(getShaSlot(player));
    }
    //播放语音
    default void voice(@NotNull PlayerEntity player, SoundEvent sound) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }
    default void voice(@NotNull PlayerEntity player, SoundEvent sound, float volume) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, volume, 1.0F);
        }
    }
    //视为类技能方法
    default void viewAs(@NotNull PlayerEntity player, TagKey<Item> tag, Item item, SoundEvent sound1, SoundEvent sound2) {
        ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
        if (!stack.isEmpty() && stack.isIn(tag)) {
            stack.decrement(1);
            player.giveItemStack(item.getDefaultStack());
            if (new Random().nextFloat() < 0.5) {voice(player, sound1);} else {voice(player, sound2);}
        }
    }
    //集智技能触发
    default void jizhi(PlayerEntity player) {
        if (hasTrinket(SkillCards.JIZHI, player)) {
            draw(player, 1);
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.JIZHI1);} else {voice(player, Sounds.JIZHI2);}
        }
    }
    //奔袭技能触发
    default void benxi(PlayerEntity player) {
        if (hasTrinket(SkillCards.BENXI, player)) {
            ItemStack stack = trinketItem(SkillCards.BENXI, player);
            NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() != null) {
                int benxi = stack.getNbt().getInt("benxi");
                if (benxi < 5) {
                    nbt.putInt("benxi", benxi + 1); stack.setNbt(nbt);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
                }
            }
        }
    }

    default void effectChange(LivingEntity entity, StatusEffect effect, int changeLevel, int duration) {
        if (changeLevel > 0) {
            if (entity.hasStatusEffect(effect)) {
                int amp = Objects.requireNonNull(entity.getStatusEffect(effect)).getAmplifier();
                entity.addStatusEffect(new StatusEffectInstance(effect, duration, amp + changeLevel));
            } else {entity.addStatusEffect(new StatusEffectInstance(effect, duration, changeLevel - 1));}
        }
        if (changeLevel < 0) {
            if (entity.hasStatusEffect(effect)) {
                int amp = Objects.requireNonNull(entity.getStatusEffect(effect)).getAmplifier();
                int newLevel = amp + changeLevel + 1;
                entity.removeStatusEffect(effect);
                if (newLevel >= 0) {
                    entity.addStatusEffect(new StatusEffectInstance(effect, duration, newLevel));
                }
            }
        }
    }
}
