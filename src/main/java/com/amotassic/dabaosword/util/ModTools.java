package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.event.callback.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.OpenInvPayload;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

public class ModTools {
    public static boolean noTieji(LivingEntity entity) {return !entity.hasStatusEffect(ModItems.TIEJI);}

    //判断是否有某个饰品
    public static boolean hasTrinket(Item item, LivingEntity entity) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return trinketItem(item, entity) != ItemStack.EMPTY;
            else return trinketItem(item, entity) != ItemStack.EMPTY && noTieji(entity);}
        return trinketItem(item, entity) != ItemStack.EMPTY;
    }

    public static ItemStack trinketItem(Item item, LivingEntity entity) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(entity);
        if(optionalComponent.isEmpty()) return ItemStack.EMPTY;

        TrinketComponent component = optionalComponent.get();
        return component.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(ItemStack.EMPTY);
    }

    public static List<Pair<SlotReference, ItemStack>> allTrinkets(LivingEntity entity) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(entity);
        return optional.map(TrinketComponent::getAllEquipped).orElse(Collections.emptyList());
    }

    //判断技能是否能触发（依据是否为锁定技和是否有铁骑效果）
    public static boolean canTrigger(Item item, LivingEntity entity) {
        if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return true;
        return noTieji(entity);
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
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound) {voice(entity, sound, 2);}
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

    //改了摸牌物品之后，应该不用这样了，但是它就是方便，暂且保留吧
    public static int countCards(PlayerEntity player) {return count(player, Tags.Items.CARD) + count(player, ModItems.GAIN_CARD);}

    //自定义战利品表解析
    public static Identifier parseLootTable(Identifier lootTableId) {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ModTools.class.getResourceAsStream("/data/dabaosword/" + lootTableId.getPath())));
        JsonObject o = gson.fromJson(reader, JsonObject.class);
        double totalWeight = 0;
        for (var element : o.getAsJsonArray("results")) {
            totalWeight += element.getAsJsonObject().get("weight").getAsDouble();
        }
        double randomValue = new Random().nextDouble() * totalWeight;
        double currentWeight = 0;
        for (JsonElement element : o.getAsJsonArray("results")) {
            JsonObject result = element.getAsJsonObject();
            currentWeight += result.get("weight").getAsDouble();
            if (randomValue < currentWeight) {
                return Identifier.of(result.get("item").getAsString());
            }
        }
        return Identifier.of("minecraft:air");
    }

    public static void draw(PlayerEntity player) {draw(player, 1);}
    public static void draw(PlayerEntity player, int count) {
        for (int n = 0; n<count; n++) {
            if (player.hasStatusEffect(ModItems.BINGLIANG)) {
                int amplifier = Objects.requireNonNull(player.getStatusEffect(ModItems.BINGLIANG)).getAmplifier();
                player.removeStatusEffect(ModItems.BINGLIANG);
                voice(player, SoundEvents.ENTITY_VILLAGER_NO,1);
                if (amplifier != 0) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1, amplifier - 1));
                } //如果有兵粮寸断效果就不摸牌，改为将debuff等级减一
            } else {
                var selectedId = parseLootTable(Identifier.of("dabaosword", "loot_tables/draw.json"));
                give(player, new ItemStack(Registries.ITEM.get(selectedId)));
                voice(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
            }
        }
    }

    public static void give(PlayerEntity player, ItemStack stack) {
        ItemEntity item = player.dropItem(stack, false);
        if (item == null) return;
        item.resetPickupDelay();
        item.setOwner(player.getUuid());
    }

    public static int getCD(ItemStack stack) { //获取物品的内置冷却时间
        return stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
    }

    public static void setCD(ItemStack stack, int seconds) { //设置物品的内置冷却时间
        stack.set(ModItems.CD, seconds);
    }

    public static int getTag(ItemStack stack) { //获取物品的标签的数量
        return stack.get(ModItems.TAGS) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.TAGS));
    }

    public static void setTag(ItemStack stack, int value) { //设置物品的标签的数量
        stack.set(ModItems.TAGS, value);
    }

    //转化卡牌技能通用方法
    public static void viewAs(PlayerEntity player, ItemStack skill, int CD, Predicate<ItemStack> predicate, ItemStack result, SoundEvent sound) {viewAs(player, skill,CD, predicate, 1, result, sound);}
    public static void viewAs(PlayerEntity player, ItemStack skill, int CD, Predicate<ItemStack> predicate, int count, ItemStack result, SoundEvent sound) {
        if (!player.getWorld().isClient && noTieji(player) && getCD(skill) == 0) {
            ItemStack stack = player.getOffHandStack();
            if (predicate.test(stack)) {
                setCD(skill, CD);
                stack.decrement(count);
                give(player, result);
                voice(player, sound);
            }
        }
    }

    public static void openInv(PlayerEntity player, PlayerEntity target, Text title, Inventory targetInv) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return new OpenInvPayload(player.getId());
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new PlayerInvScreenHandler(syncId, targetInv, target);
                }
            });
        }
    }

    public static Inventory targetInv(PlayerEntity target, Boolean equip, Boolean armor, int cards, ItemStack eventStack) {
        /*
        Boolean equip: 是否显示装备牌
        Boolean armor: 是否显示玩家的盔甲
        int cards: 是否显示手牌。0：完全不显示；1：显示随机选取手牌；2：显示所有手牌；3：显示所有物品
        */
        Inventory targetInv = new SimpleInventory(60);
        if(equip) {
            for(var entry : allTrinkets(target)) {
                ItemStack stack = entry.getRight();
                if (stack.streamTags().toList().equals(ModItems.GUDING_WEAPON.getDefaultStack().streamTags().toList())) targetInv.setStack(0, stack);
                if (stack.streamTags().toList().equals(ModItems.BAGUA.getDefaultStack().streamTags().toList())) targetInv.setStack(1, stack);
                if (stack.getItem() == ModItems.DILU) targetInv.setStack(2, stack);
                if (stack.getItem() == ModItems.CHITU) targetInv.setStack(3, stack);
            }//四件装备占1~4格
        }

        List<ItemStack> armors = List.of(target.getEquippedStack(EquipmentSlot.HEAD), target.getEquippedStack(EquipmentSlot.CHEST), target.getEquippedStack(EquipmentSlot.LEGS), target.getEquippedStack(EquipmentSlot.FEET));
        for (ItemStack stack : armors) {
            if (armor && !stack.isEmpty()) targetInv.setStack(armors.indexOf(stack) + 4, stack);
        }//4件盔甲占5~8格

        DefaultedList<ItemStack> targetInventory = target.getInventory().main;
        List<Integer> cardSlots = IntStream.range(0, targetInventory.size()).filter(i -> isCard(targetInventory.get(i))).boxed().toList();
        if (cards == 2 && !cardSlots.isEmpty()) {
            for(Integer i : cardSlots) {
                targetInv.setStack(i + 9, targetInventory.get(i));
            }
        }//副手物品在第9格，其他背包中的物品依次排列
        ItemStack off = target.getOffHandStack();
        if (cards == 2 && isCard(off)) targetInv.setStack(8, off);
        if (cards == 3) {
            for (ItemStack stack : targetInventory) {
                if (!stack.isEmpty()) targetInv.setStack(targetInventory.indexOf(stack) + 9, stack);
            }
            targetInv.setStack(8, off);
        }
        targetInv.setStack(54, new ItemStack(ModItems.GAIN_CARD, cards));//用于传递显示卡牌信息
        targetInv.setStack(55, eventStack);//用于传递stack信息
        return targetInv;
    }

    public static void openSimpleMenu(PlayerEntity player, PlayerEntity target, Inventory inventory, Text title) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return new OpenInvPayload(player.getId());
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new SimpleMenuHandler(syncId, inventory, target);
                }
            });
        }
    }

    public static void cardUsePost(PlayerEntity user, ItemStack stack, @Nullable LivingEntity target) {
        CardCBs.USE_POST.invoker().cardUsePost(user, stack, target);
    }

    public static void cardDiscard(PlayerEntity player, ItemStack stack, int count, boolean fromEquip) {
        CardCBs.DISCARD.invoker().cardDiscard(player, stack, count, fromEquip);
    }

    public static void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, CardCBs.T type) {
        CardCBs.MOVE.invoker().cardMove(from, to, stack, count, type);
    }

}
