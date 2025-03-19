package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.Card;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.ISha;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static dev.emi.trinkets.api.TrinketsApi.getTrinketComponent;

@SuppressWarnings("unused")
public class ModTools {
    //通过predicate寻找对应物品，免得添加标签
    public static Predicate<ItemStack> p(Item item) {return s -> s.isOf(item);}
    //判断是否是卡牌
    public static boolean isCard(ItemStack s) {return s.getItem() instanceof Card card && card.getType() != null;}
    public static final Predicate<ItemStack>
    canSaveDying = p(ModItems.JIU).or(p(ModItems.PEACH)),
    isSha = s -> s.getItem() instanceof ISha,
    isCard = ModTools::isCard,
    isBasic = s -> s.getItem() instanceof Card c && c.getType() == Card.Type.BASIC,
    isArmoury = s -> s.getItem() instanceof Card c && c.getType() == Card.Type.ARMOURY,
    isEquipment = s -> s.getItem() instanceof Card c && c.getType() == Card.Type.EQUIPMENT,
    isDiamondCard = s -> getSuit(s) == Card.Suits.Diamond,
    isHeartCard = s -> getSuit(s) == Card.Suits.Heart,
    isClubCard = s -> getSuit(s) == Card.Suits.Club,
    isSpadeCard = s -> getSuit(s) == Card.Suits.Spade,
    isRedCard = isDiamondCard.or(isHeartCard),
    isBlackCard = isClubCard.or(isSpadeCard);
    public static boolean isWanjian(DamageSource source) {
        return source.getSource() instanceof ArrowEntity arrow && arrow.getCommandTags().contains("a");
    }
    public static boolean isHuogong(DamageSource source) {
        return source.getSource() instanceof FireballEntity fireball && fireball.getCommandTags().contains("a");
    }
    public static boolean isShandian(DamageSource source) {
        return source.getSource() instanceof LightningEntity lightning && lightning.getCommandTags().contains("a");
    }

    public static boolean noTieji(LivingEntity entity) {return !entity.hasStatusEffect(ModItems.TIEJI);}

    /**判断是否有某个饰品*/
    public static boolean hasTrinket(Item item, LivingEntity entity) {
        if (item instanceof SkillItem) {
            if (item.getDefaultStack().isIn(Tags.LOCK_SKILL)) return !trinketItem(item, entity).isEmpty();
            else return !trinketItem(item, entity).isEmpty() && noTieji(entity);}
        return !trinketItem(item, entity).isEmpty();
    }
    public static boolean isEquipped(LivingEntity entity, Predicate<ItemStack> p) {
        return getTrinketComponent(entity).map(c -> c.isEquipped(p)).orElse(false);
    }

    public static ItemStack trinketItem(Item item, LivingEntity entity) {
        return getTrinketComponent(entity).map(c -> c.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    /**获取该实体的所有饰品，输出为ItemStack列表*/
    public static List<ItemStack> allTrinkets(LivingEntity entity) {
        return getTrinketComponent(entity).map(c -> c.getAllEquipped().stream().map(Pair::getRight).toList()).orElse(Collections.emptyList());
    }

    /**判断技能是否能触发（依据是否为锁定技和是否有铁骑效果）*/
    public static boolean canTrigger(ItemStack item, LivingEntity entity) {
        if (item.getItem() instanceof SkillItem) {
            if (item.isIn(Tags.LOCK_SKILL)) return true;
            return noTieji(entity);
        } return true;
    }

    public static CardPileInventory getCardPack(PlayerEntity player) {
        return PVPGameEvents.PLAYER_CARD_PACKS.getOrDefault((ServerPlayerEntity) player, new CardPileInventory(player));
    }

    /**判断牌堆和背包中是否有符合条件的卡牌*/
    public static boolean hasCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getCard(entity, predicate).isEmpty();
    }
    /**获取牌堆或背包中的一张符合条件的卡牌*/
    public static ItemStack getCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof PlayerEntity player) {
            for (ItemStack card : getCardPack(player).cards) {
                if (predicate.test(card)) return card;
            }
        }
        return getItem(entity, predicate);
    }

    /**判断生物是否有某个物品*/
    public static boolean hasItem(@NotNull LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getItem(entity, predicate).isEmpty();
    }
    /**获取玩家背包中第一个符合条件的物品，或者生物的符合条件的主副手物品*/
    public static ItemStack getItem(@NotNull LivingEntity entity, Predicate<ItemStack> predicate) {
        for (var stack : getItems(entity, predicate, true, false, false, false)) if (predicate.test(stack)) return stack;
        return ItemStack.EMPTY;
    }

    /**播放语音*/
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound) {voice(entity, sound, 2);}
    public static void voice(@NotNull LivingEntity entity, SoundEvent sound, float volume) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, volume, 1.0F);
        }
    }
    public static void voice(@NotNull LivingEntity entity, ItemStack stack) {
        SoundEvent sound = Registries.SOUND_EVENT.get(Identifier.of(stack.getItem().toString()));
        if (sound != null) voice(entity, sound);
    }
    public static SoundEvent getSound(String name) {
        return Registries.SOUND_EVENT.get(Identifier.of("dabaosword", name));
    }

    /**数玩家所有手牌的数量*/
    public static int countCards(LivingEntity entity) {return countCard(entity, isCard);}
    /**数玩家所有卡牌，包括已装备的牌*/
    public static int countAllCards(LivingEntity entity) {return count(entity, isCard, false, true, true);}
    /**数玩家牌堆背包和物品栏的卡牌*/
    public static int countCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        return count(entity, predicate, false, true, false);
    }
    /**计数*/
    public static int count(LivingEntity entity, Predicate<ItemStack> predicate, boolean armor, boolean pile, boolean trinkets) {
        int n = 0;
        for (var stack : getItems(entity, predicate, true, armor, trinkets, pile)) n += stack.getCount();
        return n;
    }

    /**将一个生物的所有符合条件的物品整理成一个list
     * @param main 如果是玩家，包括玩家的物品栏和副手物品，否则只包括生物的主副手物品*/
    public static List<ItemStack> getItems(LivingEntity entity, Predicate<ItemStack> p, boolean main, boolean armor, boolean trinket, boolean pile) {
        List<ItemStack> items = new ArrayList<>();
        //如果是玩家则把牌堆中的物品添加到待选物品中
        if (pile && entity instanceof PlayerEntity player) for (var stack : getCardPack(player).cards) if (p.test(stack)) items.add(stack);
        if (main) { //如果是玩家则把背包和副手的物品添加到待选物品中，否则只添加主副手物品
            if (entity instanceof PlayerEntity player) {
                for (var stack : player.getInventory().main) if (p.test(stack)) items.add(stack);
            } else if (p.test(entity.getMainHandStack())) items.add(entity.getMainHandStack());
            if (p.test(entity.getOffHandStack())) items.add(entity.getOffHandStack());
        }
        if (armor) for (var stack : entity.getArmorItems()) if (p.test(stack)) items.add(stack);
        if (trinket) for (var stack : allTrinkets(entity)) if (p.test(stack)) items.add(stack);
        return items;
    }

    public static void draw(LivingEntity entity) {draw(entity, 1);}
    public static void draw(LivingEntity entity, int count) {
        for (int n = 0; n<count; n++) {
            if (entity.hasStatusEffect(ModItems.BINGLIANG)) {
                int amplifier = Objects.requireNonNull(entity.getStatusEffect(ModItems.BINGLIANG)).getAmplifier();
                entity.removeStatusEffect(ModItems.BINGLIANG);
                voice(entity, SoundEvents.ENTITY_VILLAGER_NO,1);
                if (amplifier != 0) {
                    entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, -1, amplifier - 1));
                } //如果有兵粮寸断效果就不摸牌，改为将debuff等级减一
            } else {
                give(entity, newCard());
                voice(entity, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1);
            }
        }
    }

    public static ItemStack customLoot(LivingEntity entity, String path) {
        var key = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("dabaosword", path));
        AtomicReference<ItemStack> stack = new AtomicReference<>(ItemStack.EMPTY);
        entity.forEachGiftedItem(world(entity), key, (world, s) -> stack.set(s));
        return stack.get();
    }

    public static ItemStack newCard() {
        return ALL_CARDS.get(new Random().nextInt(ALL_CARDS.size())).copy();
    }
    public static ItemStack newCard(Item item) {return newCard(p(item));}
    public static ItemStack newCard(Predicate<ItemStack> predicate) {
        List<ItemStack> list = ALL_CARDS.stream().filter(predicate).toList();
        if (list.isEmpty()) return ItemStack.EMPTY;
        return list.get(new Random().nextInt(list.size())).copy();
    }

    public static void give(LivingEntity entity, ItemStack stack) {
        if (entity instanceof PlayerEntity player) {
            ItemEntity item = player.dropItem(stack, false);
            if (item == null) return;
            item.setInvulnerable(true);
            item.resetPickupDelay();
            item.setOwner(player.getUuid());
            return;
        }
        if (entity.getMainHandStack().isEmpty()) entity.setStackInHand(Hand.MAIN_HAND, stack);
        else if (entity.getOffHandStack().isEmpty()) entity.setStackInHand(Hand.OFF_HAND, stack);
    }

    private static final List<ItemStack> ALL_CARDS = new ArrayList<>();

    public static void initAllCards() {
        for (Item item : ModItems.CARDS) {
            String path = Registries.ITEM.getId(item).getPath() + ".json";
            Gson gson = new Gson();
            InputStream stream = ModTools.class.getResourceAsStream("/data/dabaosword/default_suit_and_rank/" + path);
            if (stream == null) continue;

            InputStreamReader reader = new InputStreamReader(stream);
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            var srs = json.get("suits_and_ranks").getAsJsonArray();
            for (int j = 0; j < srs.size(); j++) {
                JsonObject sr = srs.get(j).getAsJsonObject();
                String suit = sr.get("suit").getAsString();
                String rank = sr.get("rank").getAsString();

                ItemStack stack = new ItemStack(item);
                var nbt = new NbtCompound(); var list = new NbtList(); var compound = new NbtCompound();
                compound.putString("Suit", Card.Suits.valueOf(suit).suit);
                compound.putString("Rank", rank);
                list.add(compound);
                nbt.put("Card", list);
                setNbt(stack, nbt);
                ALL_CARDS.add(stack);
            }
        }
        System.out.println("Loaded " + ALL_CARDS.size() + " cards");
    }

    public static Pair<Card.Suits, Card.Ranks> getSuitAndRank(ItemStack stack) {
        if (isCard(stack)) {
            NbtCompound compound = getOrCreateNbt(stack);
            if (compound.contains("Card")) {
                NbtCompound nbtCompound = ((NbtList) Objects.requireNonNull(compound.get("Card"))).getCompound(0);
                Card.Suits suit = Card.Suits.get(nbtCompound.getString("Suit"));
                Card.Ranks rank = Card.Ranks.get(nbtCompound.getString("Rank"));
                return new Pair<>(suit, rank);
            }
        }
        return null;
    }
    public static Card.Suits getSuit(ItemStack stack) {
        var sr = getSuitAndRank(stack);
        if (sr == null) return null; return sr.getLeft();
    }
    public static Card.Ranks getRank(ItemStack stack) {
        var sr = getSuitAndRank(stack);
        if (sr == null) return null; return sr.getRight();
    }
    /**仿照1.20代码写的获取物品NBT的方法*/
    public static NbtCompound getOrCreateNbt(ItemStack stack) {
        if (stack.isEmpty()) return new NbtCompound();
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) setNbt(stack, new NbtCompound());
        return Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
    }
    public static void setNbt(ItemStack stack, NbtCompound nbt) {
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
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

    public static void openInv(PlayerEntity player, PlayerEntity target, Text title, ItemStack stack, boolean openSelfInv, boolean equip, boolean armor, int cards) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return new ActiveSkillPayload(player.getId());
                }

                @Override
                public Text getDisplayName() {return title;}

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    PlayerEntity invOwner = openSelfInv ? player : target;
                    return new PlayerInvScreenHandler(syncId, targetInv(invOwner, equip, armor, cards, stack, openSelfInv), target);
                }
            });
        }
    }

    public static Inventory targetInv(PlayerEntity invOwner, Boolean equip, Boolean armor, int cards, ItemStack eventStack, boolean openSelfInv) {
        /*
        Boolean equip: 是否显示装备牌
        Boolean armor: 是否显示玩家的盔甲
        int cards: 是否显示手牌。0：完全不显示；1：显示随机选取手牌；2：显示所有手牌；3：显示所有物品
        */
        Inventory targetInv = new SimpleInventory(60);
        if (equip) {
            for (var stack : allTrinkets(invOwner)) {
                if (stack.streamTags().toList().equals(ModItems.GUDING_WEAPON.getDefaultStack().streamTags().toList())) targetInv.setStack(0, stack);
                if (stack.streamTags().toList().equals(ModItems.BAGUA.getDefaultStack().streamTags().toList())) targetInv.setStack(1, stack);
                if (stack.isOf(ModItems.DILU)) targetInv.setStack(2, stack);
                if (stack.isOf(ModItems.CHITU)) targetInv.setStack(3, stack);
            }//四件装备占1~4格
        }

        ItemStack off = invOwner.getOffHandStack();
        List<ItemStack> armors = List.of(invOwner.getEquippedStack(EquipmentSlot.HEAD), invOwner.getEquippedStack(EquipmentSlot.CHEST), invOwner.getEquippedStack(EquipmentSlot.LEGS), invOwner.getEquippedStack(EquipmentSlot.FEET));
        for (ItemStack stack : armors) {
            if (armor && !stack.isEmpty()) targetInv.setStack(armors.indexOf(stack) + 4, stack);
        }//4件盔甲占5~8格

        DefaultedList<ItemStack> inv = invOwner.getInventory().main;
        List<Integer> cardSlots = IntStream.range(0, inv.size()).filter(i -> isCard(inv.get(i))).boxed().toList();
        if (cards == 2) {
            var inventory = getCardPack(invOwner).cards;
            for (var stack : inventory) targetInv.setStack(inventory.indexOf(stack) + 9, stack);
            if (!cardSlots.isEmpty()) { //卡牌背包中的牌显示在中间36格
                for(Integer i : cardSlots) {
                    if (i > 8) break; //快捷栏的牌显示在最底层9格
                    targetInv.setStack(i + 45, inv.get(i));
                }
            }
            if (isCard(off)) targetInv.setStack(8, off);
        }
        if (cards == 3) {
            for (ItemStack stack : inv) if (!stack.isEmpty()) targetInv.setStack(inv.indexOf(stack) + 9, stack);
            targetInv.setStack(8, off);
        }
        targetInv.setStack(54, new ItemStack(ModItems.GAIN_CARD, cards));//用于传递显示卡牌信息
        targetInv.setStack(55, eventStack);//用于传递stack信息
        if (openSelfInv) targetInv.setStack(56, new ItemStack(ModItems.GAIN_CARD));
        return targetInv;
    }

    public static void openSimpleMenu(PlayerEntity player, PlayerEntity target, Inventory inventory, Text title) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return new ActiveSkillPayload(target.getId());
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

    public static void closeGUI(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }

    public static <T> RegistryEntry<T> getEntry(Entity entity, RegistryKey<T> key, RegistryKey<Registry<T>> ref) {
        var registry = world(entity).getRegistryManager().getOrThrow(ref);
        return registry.getEntry(registry.get(key));
    }

    public static DamageSource damageSource(Entity source, RegistryKey<DamageType> type) {
        return new DamageSource(getEntry(source, type, RegistryKeys.DAMAGE_TYPE), source);
    }

    public static void writeDamage(DamageSource source, float amount, boolean returnShan, ItemStack stack) {
        NbtList list = new NbtList();
        NbtCompound compound = new NbtCompound();
        //noinspection OptionalGetWithoutIsPresent
        compound.putString("type", source.getTypeRegistryEntry().getKey().get().getValue().toString());
        if (source.getSource() != null) compound.putInt("source", source.getSource().getId());
        if (source.getAttacker() != null) compound.putInt("attacker", source.getAttacker().getId());
        compound.putFloat("amount", amount);
        if (returnShan) compound.putString("returning", "dabaosword:shan");
        list.add(compound);
        NbtCompound nbt = getOrCreateNbt(stack);
        nbt.put("DamageDodged", list);
        setNbt(stack, nbt);
    }
    //牌堆记录闪避伤害的方法
    public static Pair<Pair<DamageSource, Float>, ItemStack> getDamage(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld world) {
            ItemStack stack = trinketItem(ModItems.CARD_PILE, player);
            NbtCompound compound = getOrCreateNbt(stack);
            if (compound.contains("DamageDodged")) {
                NbtCompound nbt = ((NbtList) Objects.requireNonNull(compound.get("DamageDodged"))).getCompound(0);
                RegistryKey<DamageType> type = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(nbt.getString("type")));
                RegistryEntry<DamageType> entry = getEntry(player, type, RegistryKeys.DAMAGE_TYPE);
                Entity source = world.getEntityById(nbt.getInt("source"));
                Entity attacker = world.getEntityById(nbt.getInt("attacker"));
                DamageSource damageSource = new DamageSource(entry, source, attacker);
                ItemStack returning = ItemStack.EMPTY;
                if (nbt.contains("returning")) returning = new ItemStack(Registries.ITEM.get(Identifier.of(nbt.getString("returning"))));
                float amount = nbt.getFloat("amount");
                return new Pair<>(new Pair<>(damageSource, amount), returning);
            }
        }
        return null;
    }

    public static ServerWorld world(Entity entity) {return (ServerWorld) entity.getWorld();}

    /**一个用于简便执行多条服务器指令的方法*/
    public static void excuteServerCommand(Entity entity, String[] commands, boolean fromServer) {
        if (entity.getWorld() instanceof ServerWorld world) {
            var server = world.getServer();
            var dispatcher = server.getCommandManager().getDispatcher();
            var commandSource = fromServer ? server.getCommandSource() : entity.getCommandSource(world);
            for (String command : commands) {
                if (command.startsWith("/")) command = command.substring(1);
                try {
                    var results = dispatcher.parse(command, commandSource);
                    dispatcher.execute(results);
                } catch (CommandSyntaxException e) {throw new RuntimeException(e);}
            }
        }
    }

    public static void title(ServerPlayerEntity player, Text title) {
        player.networkHandler.sendPacket(new TitleS2CPacket(title));
    }

    public static void subtitle(ServerPlayerEntity player, Text sub) {
        player.networkHandler.sendPacket(new SubtitleS2CPacket(sub));
    }
}
