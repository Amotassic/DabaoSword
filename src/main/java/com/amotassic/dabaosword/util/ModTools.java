package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.CardCBs;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.equipment.Equipment;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ModTools {
    //通过predicate寻找对应物品，免得添加标签
    public static final Predicate<ItemStack> canSaveDying = s -> s.isOf(ModItems.JIU) || s.isOf(ModItems.PEACH);
    public static final Predicate<ItemStack> isSha = s -> s.getItem() instanceof CardItem.Sha;
    public static final Predicate<ItemStack> nonBasic = s -> s.isIn(Tags.Items.CARD) && !s.isIn(Tags.Items.BASIC_CARD);
    //不立即生效而产生消耗的卡牌，也就是说需要写额外的消耗逻辑来处理
    public static final Predicate<ItemStack> notImmediatelyEffect =  s -> s.isOf(ModItems.DISCARD) || s.isOf(ModItems.STEAL);
    //判断是否是卡牌
    public static final Predicate<ItemStack> isCard = s -> s.isIn(Tags.Items.CARD);
    public static boolean isCard(ItemStack stack) {return stack.isIn(Tags.Items.CARD);}
    public static final Predicate<ItemStack> isRedCard = s -> {
        var sr = getSuitAndRank(s);
        if (sr != null) return sr.getLeft() == Card.Suits.Heart || sr.getLeft() == Card.Suits.Diamond;
        return false;
    };

    public static boolean noTieji(LivingEntity entity) {return !entity.hasStatusEffect(ModItems.TIEJI);}

    //判断是否有某个饰品
    public static boolean hasTrinket(Item item, LivingEntity entity) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.Items.LOCK_SKILL)) return !trinketItem(item, entity).isEmpty();
            else return !trinketItem(item, entity).isEmpty() && noTieji(entity);}
        return !trinketItem(item, entity).isEmpty();
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

    //判断牌堆和背包中是否有符合条件的卡牌
    public static boolean hasCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getCard(entity, predicate).getRight().isEmpty();
    }
    //获取牌堆或背包中的一张符合条件的卡牌
    public static Pair<CardPileInventory, ItemStack> getCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof PlayerEntity player) {
            CardPileInventory inventory = new CardPileInventory(player);
            for (int i = inventory.cards.size() - 1; i >= 0; i--) { //倒序检索
                var card = inventory.getStack(i);
                if (predicate.test(card)) return new Pair<>(inventory, card);
            }
        }
        return new Pair<>(null, getItem(entity, predicate));
    }

    //专为处理卡牌减少而写的方法，牌堆中的卡牌减少，需要保存nbt
    public static void cardDecrement(Pair<CardPileInventory, ItemStack> stack, int count) {
        if (stack.getLeft() == null) stack.getRight().decrement(count);
        else stack.getLeft().removeStack(stack.getRight(), count);
    }
    //另一个用于处理卡牌减少的方法
    public static void cardDecrement(LivingEntity entity, ItemStack stack, int count) {
        var pair = getCard(entity, s -> ItemStack.areEqual(s, stack));
        //如果是牌堆中的卡牌，需要调用牌堆的方法来减少，以保存nbt
        if (pair.getLeft() != null) pair.getLeft().removeStack(pair.getRight(), count);
        //那么为什么这里没有else呢？因为此stack非彼pair.getRight()，如果不减少会出bug
        stack.decrement(count);
    }
    //卡牌使用后减少，不需要传入原始的itemStack
    public static void cardUseAndDecrement(LivingEntity user, ItemStack card) {
        //即使创造模式，无懈可击也会消耗，为什么呢？我也不知道
        if (card.isOf(ModItems.WUXIE)) cardDecrement(getCard(user, s -> s.isOf(ModItems.WUXIE)), 1);
        else {
            if (user instanceof PlayerEntity player && player.getAbilities().creativeMode) return;
            cardDecrement(getCard(user, s -> s.isOf(card.getItem())), 1);
        }
    }
    //顾名思义，就是没有触发UsePre事件，因此需要这个方法来扣除卡牌
    public static void nonPreUseCardDecrement(LivingEntity user, ItemStack stack, LivingEntity target) {
        var card = stack.copy();
        cardUseAndDecrement(user, stack);
        cardUsePost(user, card, target);
    }

    //判断生物是否有某个物品
    public static boolean hasItem(@NotNull LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getItem(entity, predicate).isEmpty();
    }
    //获取玩家背包中第一个符合条件的物品，或者生物的符合条件的主副手物品
    public static ItemStack getItem(@NotNull LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof PlayerEntity player) {
            for (int i = 0; i < player.getInventory().size(); ++i) {
                ItemStack stack = player.getInventory().getStack(i);
                if (predicate.test(stack)) return stack;
            }
        } else {
            if (predicate.test(entity.getMainHandStack())) return entity.getMainHandStack();
            if (predicate.test(entity.getOffHandStack())) return entity.getOffHandStack();
        }
        return ItemStack.EMPTY;
    }

    //播放语音
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound) {voice(entity, sound, 2);}
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound, float volume) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, volume, 1.0F);
        }
    }

    //数玩家所有牌的数量
    public static int countCards(PlayerEntity player) {return countCard(player, isCard);}
    //数玩家牌堆背包和物品栏的卡牌
    public static int countCard(PlayerEntity player, Predicate<ItemStack> predicate) {
        int n = 0;
        for (var card : new CardPileInventory(player).cards) {if (predicate.test(card)) n += card.getCount();}
        n += count(player, predicate);
        return n;
    }
    //只数玩家物品栏里的物品
    public static int count(PlayerEntity player, Predicate<ItemStack> predicate) {
        PlayerInventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (predicate.test(stack)) n += stack.getCount();
        }
        return n;
    }

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
                return new Identifier(result.get("item").getAsString());
            }
        }
        return new Identifier("minecraft:air");
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
                var selectedId = parseLootTable(new Identifier("dabaosword", "loot_tables/draw.json"));
                give(player, new ItemStack(Registries.ITEM.get(selectedId)));
                voice(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
            }
        }
    }

    public static void give(PlayerEntity player, ItemStack stack) {
        initSuitsAndRanks(stack); //仅有通过这个方法获得的牌才会有花色和点数
        ItemEntity item = player.dropItem(stack, false);
        if (item == null) return;
        item.resetPickupDelay();
        item.setOwner(player.getUuid());
    }

    public static Pair<String, String> getDefaultOrRandomSuitAndRank(ItemStack stack) {
        //在json文件中为了方便读写，花色用了Suits.name()，点数用了Ranks.rank
        Pair<String, String> random = new Pair<>(Card.Suits.get("0").name(), Card.Ranks.get("0").rank);
        //在1.21之前的版本中，物品的toString()方法返回的是物品的path
        //在1.21版本中，物品的toString()方法返回的是物品的namespace:path
        String srPath = stack.getItem().toString() + ".json";
        Gson gson = new Gson();
        InputStream stream = ModTools.class.getResourceAsStream("/data/dabaosword/default_suit_and_rank/" + srPath);
        if (stream == null) return random;

        InputStreamReader reader = new InputStreamReader(stream);
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        int size = json.getAsJsonArray("suits_and_ranks").size();
        //随机获取一组花色和点数
        JsonObject obj = json.getAsJsonArray("suits_and_ranks").get(new Random().nextInt(size)).getAsJsonObject();
        if (obj.has("suit") && obj.has("rank")) {
            return new Pair<>(obj.get("suit").getAsString(), obj.get("rank").getAsString());
        } else return random;
    }

    public static void initSuitsAndRanks(ItemStack stack) {
        if (isCard(stack)) {
            NbtCompound nbt = stack.getOrCreateNbt();
            if (nbt.contains("Card")) return;
            NbtList list = new NbtList();
            NbtCompound compound = new NbtCompound();
            var suitAndRank = getDefaultOrRandomSuitAndRank(stack);
            compound.putString("Suit", Card.Suits.valueOf(suitAndRank.getLeft()).suit);
            compound.putString("Rank", suitAndRank.getRight());
            list.add(compound);
            nbt.put("Card", list);
            stack.setNbt(nbt);
        }
    }

    public static Pair<Card.Suits, Card.Ranks> getSuitAndRank(ItemStack stack) {
        if (isCard(stack)) {
            NbtCompound compound = stack.getOrCreateNbt();
            if (compound.contains("Card")) {
                NbtCompound nbtCompound = ((NbtList) Objects.requireNonNull(compound.get("Card"))).getCompound(0);
                Card.Suits suit = Card.Suits.get(nbtCompound.getString("Suit"));
                Card.Ranks rank = Card.Ranks.get(nbtCompound.getString("Rank"));
                return new Pair<>(suit, rank);
            }
        }
        return null;
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

    public static void openInv(PlayerEntity player, PlayerEntity target, Text title, ItemStack stack, Inventory targetInv) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeUuid(target.getUuid());
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new PlayerInvScreenHandler(syncId, targetInv, target, stack);
                }
            });
        }
    }

    public static Inventory targetInv(PlayerEntity target, Boolean equip, Boolean armor, int cards) {
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
                if (stack.isOf(ModItems.DILU)) targetInv.setStack(2, stack);
                if (stack.isOf(ModItems.CHITU)) targetInv.setStack(3, stack);
            }//四件装备占1~4格
        }

        List<ItemStack> armors = List.of(target.getEquippedStack(EquipmentSlot.HEAD), target.getEquippedStack(EquipmentSlot.CHEST), target.getEquippedStack(EquipmentSlot.LEGS), target.getEquippedStack(EquipmentSlot.FEET));
        for (ItemStack stack : armors) {
            if (armor && !stack.isEmpty()) targetInv.setStack(armors.indexOf(stack) + 4, stack);
        }//4件盔甲占5~8格

        ItemStack off = target.getOffHandStack();
        DefaultedList<ItemStack> targetInventory = target.getInventory().main;
        List<Integer> cardSlots = IntStream.range(0, targetInventory.size()).filter(i -> isCard(targetInventory.get(i))).boxed().toList();
        if (cards == 2) {
            var inventory = new CardPileInventory(target).cards;
            for (var stack : inventory) {targetInv.setStack(inventory.indexOf(stack) + 9, stack);}
            if (!cardSlots.isEmpty()) { //卡牌背包中的牌显示在中间36格
                for(Integer i : cardSlots) {
                    if (i > 8) break; //快捷栏的牌显示在最底层9格
                    targetInv.setStack(i + 45, targetInventory.get(i));
                }
            }
            if (isCard(off)) targetInv.setStack(8, off);
        }
        if (cards == 3) {
            for (ItemStack stack : targetInventory) {
                if (!stack.isEmpty()) targetInv.setStack(targetInventory.indexOf(stack) + 9, stack);
            }
            targetInv.setStack(8, off);
        }
        targetInv.setStack(54, new ItemStack(ModItems.GAIN_CARD, cards));
        return targetInv;
    }

    public static void openSimpleMenu(PlayerEntity player, ItemStack stack, Inventory inventory, Text title) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(stack);
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new SimpleMenuHandler(syncId, inventory, stack);
                }
            });
        }
    }

    public static boolean cardUsePre(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        return CardCBs.USE_PRE.invoker().cardUsePre(user, stack, target);
    }

    public static void cardUsePost(LivingEntity user, ItemStack stack, @Nullable LivingEntity target) {
        CardCBs.USE_POST.invoker().cardUsePost(user, stack, target);
    }

    /**
     * 以下两个调用事件监听器的方法，除非stack来自牌堆背包，否则一定要传入原始的stack！
     * 因为还没有真正到事件触发时就已经移除了卡牌，所以事件中使用的stack是复制的！
     * */

    public static void cardDiscard(LivingEntity entity, ItemStack stack, int count, boolean fromEquip) {
        ItemStack copy = stack.copyWithCount(count);
        //移除被弃置的牌
        cardDecrement(entity, stack, count);
        CardCBs.DISCARD.invoker().cardDiscard(entity, copy, count, fromEquip);
    }

    public static void cardMove(LivingEntity from, PlayerEntity to, ItemStack stack, int count, CardCBs.T type) {
        ItemStack copy = stack.copyWithCount(count);
        //移除来源的牌
        cardDecrement(from, stack, count);
        //如果是移动到物品栏的类型，则给to等量的物品
        if (type == CardCBs.T.INV_TO_INV || type == CardCBs.T.EQUIP_TO_INV) give(to, copy);
        //如果是移动到装备栏的类型，则目标使用或替换该装备
        if (type == CardCBs.T.INV_TO_EQUIP || type == CardCBs.T.EQUIP_TO_EQUIP) Equipment.useOrReplaceEquip(to, copy);
        CardCBs.MOVE.invoker().cardMove(from, to, copy, count, type);
    }

    public static void writeDamage(DamageSource source, float amount, boolean returnShan, ItemStack stack) {
        NbtList list = new NbtList();
        NbtCompound compound = new NbtCompound();
        compound.putString("type", source.getTypeRegistryEntry().getKey().get().getValue().toString());
        if (source.getSource() != null) compound.putInt("source", source.getSource().getId());
        if (source.getAttacker() != null) compound.putInt("attacker", source.getAttacker().getId());
        compound.putFloat("amount", amount);
        if (returnShan) compound.putString("returning", "dabaosword:shan");
        list.add(compound);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("DamageDodged", list);
        stack.setNbt(nbt);
    }
    //牌堆记录闪避伤害的方法
    public static Pair<Pair<DamageSource, Float>, ItemStack> getDamage(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            ItemStack stack = trinketItem(ModItems.CARD_PILE, player);
            NbtCompound compound = stack.getOrCreateNbt();
            if (compound.contains("DamageDodged")) {
                NbtCompound nbt = ((NbtList) Objects.requireNonNull(compound.get("DamageDodged"))).getCompound(0);
                RegistryKey<DamageType> type = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(nbt.getString("type")));
                RegistryEntry<DamageType> entry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type);
                Entity source = world.getEntityById(nbt.getInt("source"));
                Entity attacker = world.getEntityById(nbt.getInt("attacker"));
                DamageSource damageSource = new DamageSource(entry, source, attacker);
                ItemStack returning = ItemStack.EMPTY;
                if (nbt.contains("returning")) returning = new ItemStack(Registries.ITEM.get(new Identifier(nbt.getString("returning"))));
                float amount = nbt.getFloat("amount");
                return new Pair<>(new Pair<>(damageSource, amount), returning);
            }
        }
        return null;
    }

}
