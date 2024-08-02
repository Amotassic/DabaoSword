package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
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
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;

public class ModTools {
    public static boolean noTieji(LivingEntity entity) {
        return !entity.hasStatusEffect(ModItems.TIEJI);
    }

    //判断是否有某个饰品
    public static boolean hasTrinket(Item item, PlayerEntity player) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return trinketItem(item, player) != null;
            else return trinketItem(item, player) != null && noTieji(player);}
        return trinketItem(item, player) != null;
    }

    public static ItemStack trinketItem(Item item, PlayerEntity player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return null;

        TrinketComponent component = optionalComponent.get();
        return component.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(null);
    }

    //判断是否有某些饰品（以数组形式判断）中的一个
    public static boolean hasTrinkets(Item[] items, PlayerEntity player) {
        for (Item item : items) {
            if (trinketItem(item, player) != null) return true;
        }
        return false;
    }

    //判断玩家是否有某个物品
    public static boolean hasItem(@NotNull PlayerEntity player, @NotNull Item item) {
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != item) continue;
            return true;
        }
        return false;
    }
    //移除玩家的1个物品
    public static void removeItem(@NotNull PlayerEntity player, @NotNull Item item) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        inv.removeStack(i, 1);
    }

    //判断是否是非基本牌
    public static boolean nonBasic(ItemStack stack) {
        return stack.isIn(Tags.Items.CARD) && !stack.isIn(Tags.Items.BASIC_CARD);
    }

    public static ItemStack stackWith(Item item, PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        return inv.getStack(i);
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
        if (entity.getWorld() instanceof ServerWorld world) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound, float volume) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, volume, 1.0F);
        }
    }
    //视为类技能方法
    public static void viewAs(@NotNull PlayerEntity player, TagKey<Item> tag, Item item, SoundEvent sound1, SoundEvent sound2) {
        ItemStack stack = player.getStackInHand(Hand.OFF_HAND);
        if (!stack.isEmpty() && stack.isIn(tag)) {
            stack.decrement(1);
            give(player, item.getDefaultStack());
            if (new Random().nextFloat() < 0.5) {voice(player, sound1);} else {voice(player, sound2);}
        }
    }
    //集智技能触发
    public static void jizhi(PlayerEntity player) {
        if (hasTrinket(SkillCards.JIZHI, player)) {
            draw(player, 1);
            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.JIZHI1);} else {voice(player, Sounds.JIZHI2);}
        }
    }
    //奔袭技能触发
    public static void benxi(PlayerEntity player) {
        if (hasTrinket(SkillCards.BENXI, player)) {
            ItemStack stack = trinketItem(SkillCards.BENXI, player);
            int benxi = getTag(stack);
            if (benxi < 5) {
                benxi ++; setTag(stack, benxi);
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
            }
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
