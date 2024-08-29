package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModTools {
    public static boolean noTieji(LivingEntity entity) {return !entity.hasStatusEffect(ModItems.TIEJI);}

    //判断是否有某个饰品
    public static boolean hasTrinket(Item item, LivingEntity entity) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return trinketItem(item, entity) != null;
            else return trinketItem(item, entity) != null && noTieji(entity);}
        return trinketItem(item, entity) != null;
    }

    public static ItemStack trinketItem(Item item, LivingEntity entity) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(entity);
        if(optionalComponent.isEmpty()) return null;

        TrinketComponent component = optionalComponent.get();
        return component.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(null);
    }

    //判断玩家是否有某个物品
    public static boolean hasItem(@NotNull PlayerEntity player, @NotNull Item item) {
        return getItem(player, item) != ItemStack.EMPTY;
    }

    public static ItemStack getItem(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) return stack;
        }
        return ItemStack.EMPTY;
    }

    //判断是否是非基本牌
    public static boolean nonBasic(ItemStack stack) {
        return stack.isIn(Tags.Items.CARD) && !stack.isIn(Tags.Items.BASIC_CARD);
    }

    //判断是否是卡牌，包含GAIN_CARD
    public static boolean isCard(ItemStack stack) {
        return stack.isIn(Tags.Items.CARD) || stack.getItem() == ModItems.GAIN_CARD;
    }

    //判断是否有含某个标签的物品
    public static Boolean hasItemInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        return player.getInventory().contains(tag);
    }

    public static int getSlotInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(tag)) continue;
            return i;
        }
        return -1;
    }

    //获取背包中第一个含有含某个标签的物品
    public static ItemStack stackInTag(TagKey<Item> tag, @NotNull PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = getSlotInTag(tag, player);
        return inv.getStack(i);
    }

    public static int getShaSlot(@NotNull PlayerEntity player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(Tags.Items.SHA)) continue;
            return i;
        }
        return -1;
    }
    //只检测玩家物品栏前18格是否有杀
    public static ItemStack shaStack(@NotNull PlayerEntity player) {
        return player.getInventory().getStack(getShaSlot(player));
    }

    //播放语音
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound) {
        voice(entity, sound, 2);
    }
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound, float volume) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, volume, 1.0F);
        }
    }

    public static int count(PlayerEntity player, TagKey<Item> tag) {
        PlayerInventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isIn(tag)) n += stack.getCount();
        }
        return n;
    }

    public static int count(PlayerEntity player, Item item) {
        PlayerInventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) n += stack.getCount();
        }
        return n;
    }

    public static void give(PlayerEntity player, ItemStack stack) {
        ItemEntity item = player.dropItem(stack, false);
        if (item == null) return;
        item.resetPickupDelay();
        item.setOwner(player.getUuid());
    }

    public static int getCD(ItemStack stack) { //获取物品的内置冷却时间
        return stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
    }

    public static void setCD(ItemStack stack, int seconds) { //设置物品的内置冷却时间
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("cooldown", seconds);
        stack.setNbt(nbt);
    }

    public static int getTag(ItemStack stack) { //获取物品的标签的数量
        return stack.getNbt() == null ? 0 : stack.getNbt().getInt("tags");
    }

    public static void setTag(ItemStack stack, int value) { //设置物品的标签的数量
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("tags", value);
        stack.setNbt(nbt);
    }

}
