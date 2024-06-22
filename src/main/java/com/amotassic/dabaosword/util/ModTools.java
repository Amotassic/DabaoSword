package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;

public interface ModTools {
    default boolean noTieji(LivingEntity entity) {
        return !entity.hasStatusEffect(ModItems.TIEJI);
    }

    //判断是否有某个饰品
    default boolean hasTrinket(Item item, PlayerEntity player) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return trinketItem(item, player) != null;
            else return trinketItem(item, player) != null && noTieji(player);}
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
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != item) continue;
            return true;
        }
        return false;
    }
    //移除玩家的1个物品
    default void removeItem(@NotNull PlayerEntity player, @NotNull Item item) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        inv.removeStack(i, 1);
    }

    //判断是否是非基本牌
    default boolean nonBasic(ItemStack stack) {
        return stack.isIn(Tags.Items.CARD) && !stack.isIn(Tags.Items.BASIC_CARD);
    }

    default ItemStack stackWith(Item item, PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        int i = inv.getSlotWithStack(item.getDefaultStack());
        return inv.getStack(i);
    }

    //判断是否有含某个标签的物品
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

    //获取背包中第一个含有含某个标签的物品
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
    default void voice(@NotNull LivingEntity player, SoundEvent sound) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }
    default void voice(@NotNull LivingEntity player, SoundEvent sound, float volume) {
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
            if (stack.getComponents().contains(ModItems.TAGS)) {
                int benxi = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (benxi < 5) {stack.set(ModItems.TAGS, benxi + 1);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
                }
            }
        }
    }

    default int count(PlayerEntity player, TagKey<Item> tag) {
        PlayerInventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isIn(tag)) n += stack.getCount();
        }
        return n;
    }

    default int count(PlayerEntity player, Item item) {
        PlayerInventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) n += stack.getCount();
        }
        return n;
    }

    default void gainMaxHp(LivingEntity entity, int hp) {
        EntityAttributeModifier AttributeModifier = new EntityAttributeModifier(Identifier.of("max_hp"), hp, EntityAttributeModifier.Operation.ADD_VALUE);
        Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH)).updateModifier(AttributeModifier);
    }

}
