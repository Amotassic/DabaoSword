package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Rank;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.card.Sha;
import com.amotassic.dabaosword.network.OpenScreenPayload;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
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
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static dev.emi.trinkets.api.TrinketsApi.getTrinketComponent;

public class ModTools {
    public static Card c(ItemStack card) {return new Card(card);}
    /**生成一张花色和点数相同，但牌名不同的牌*/
    public static Card c(ItemStack card, CardItem replace) {
        var c = c(card);
        return new Card(replace, c.suit, c.rank);
    }
    public static Skill s(ItemStack skill) {return new Skill(skill);}
    public static ExData d() {return new ExData();}
    //通过predicate寻找对应物品，免得添加标签
    public static Predicate<ItemStack> p(Item item) {return s -> s.isOf(item);}
    //判断是否是卡牌
    public static boolean isCard(ItemStack s) {return s.getItem() instanceof CardItem;}
    public static final Predicate<ItemStack>
            canSaveDying = p(ModItems.JIU).or(p(ModItems.PEACH)),
            isSha = s -> s.getItem() instanceof Sha,
            isCard = ModTools::isCard,
            isBasic = s -> c(s).type == Card.BASIC,
            isArmoury = s -> c(s).type == Card.ARMOURY,
            isEquipment = s -> c(s).type == Card.EQUIPMENT,
            isDiamondCard = s -> c(s).suit == Suit.Diamond,
            isHeartCard = s -> c(s).suit == Suit.Heart,
            isClubCard = s -> c(s).suit == Suit.Club,
            isSpadeCard = s -> c(s).suit == Suit.Spade,
    isRedCard = isDiamondCard.or(isHeartCard),
    isBlackCard = isClubCard.or(isSpadeCard);

    public static boolean isHuogong(DamageSource source) {
        return source.getSource() instanceof FireballEntity fireball && fireball.getCommandTags().contains("a");
    }
    public static boolean isShandian(DamageSource source) {
        return source.getSource() instanceof LightningEntity lightning && lightning.getCommandTags().contains("a");
    }

    @SafeVarargs
    public static <T> List<T> toList(T... t) {return new ArrayList<>(Arrays.asList(t));}

    /**判断是否有某个饰品*/
    public static boolean hasTrinket(Item item, LivingEntity entity) {return isEquipped(entity, p(item));}
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

    public static CardPileInventory getCardPack(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity sp) return PVPGameEvents.PLAYER_CARD_PACKS.getOrDefault(sp, new CardPileInventory(player));
        return new CardPileInventory(player);
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
    public static void voice(LivingEntity entity, SoundEvent sound, float... volume) {
        if (entity.getWorld() instanceof ServerWorld world) {
            float v = volume.length > 0 ? volume[0] : 2;
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, v, 1.0F);
        }
    }
    public static void voice(LivingEntity entity, Item item, float... volume) {
        voice(entity, Registries.ITEM.getId(item).getPath(), volume);
    }
    public static void voice(LivingEntity entity, ItemStack stack, float... volume) {
        voice(entity, stack.getItem(), volume);
    }
    public static void voice(LivingEntity entity, String name, float... volume) {
        voice(entity, getSound("dabaosword", name), volume);
    }
    public static SoundEvent getSound(String namespace, String path) {
        return RegistryEntry.of(SoundEvent.of(Identifier.of(namespace, path))).value();
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

    public static void draw(LivingEntity entity, int... count) {
        int num = count.length > 0 ? count[0] : 1;
        for (int n = 0; n < num; n++) {
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

    private static final List<ItemStack> CARD_PILE = new ArrayList<>();
    public static ItemStack newCard() {
        if (CARD_PILE.isEmpty()) {
            for (ItemStack stack : ALL_CARDS) CARD_PILE.add(stack.copy());
            Collections.shuffle(CARD_PILE);
            DabaoSword.LOGGER.info("Shuffled card pile");
        }
        return CARD_PILE.removeFirst();
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
        for (CardItem item : ModItems.CARDS) {
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

                Card card = new Card(item, Suit.valueOf(suit), Rank.fromString(rank));
                ALL_CARDS.add(card.toStack());
            }
        }
        DabaoSword.LOGGER.info("Loaded {} cards", ALL_CARDS.size());
    }

    public static @Nullable <T extends Entity> T getClosestEntity(Entity entity, Class<T> clazz, double boxLength, Predicate<T> predicate) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box box = new Box(entity.getBlockPos()).expand(boxLength);
            List<T> entities = world.getEntitiesByClass(clazz, box, predicate.and(e -> e != entity));
            if (!entities.isEmpty()) {
                Map<Float, T> map = new HashMap<>();
                for (var e : entities) {
                    map.put(e.distanceTo(entity), e);
                }
                float min = Collections.min(map.keySet());
                return map.get(min);
            }
        }
        return null;
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

    public static void openFullInv(PlayerEntity player, LivingEntity target, boolean editable) {
        if (player.getWorld().isClient) return;
        var payload = new OpenScreenPayload(target.getId(), editable, "");
        player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            public Text getDisplayName() {return target.getDisplayName();}

            public Object getScreenOpeningData(ServerPlayerEntity player) {return payload;}

            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new FullInvScreenHandler(syncId, inv, payload);
            }
        });
    }

    public static void openInv(PlayerEntity player, LivingEntity owner, PlayerEntity target, Text title, ItemStack stack, boolean equip, boolean armor, int cards) {
        if (player.getWorld().isClient) return;
        var tempInv = new TempInventory(player, owner, stack, cards, equip, armor);
        var rows = tempInv.rowsToShow;
        player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            public Text getDisplayName() {return title;}

            public Object getScreenOpeningData(ServerPlayerEntity player) {
                return new OpenScreenPayload(target.getId(), true, rows.toString());
            }
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new PlayerInvScreenHandler(syncId, tempInv, target, rows);
            }
        });
    }

    public static void openMenu(PlayerEntity player, PlayerEntity target, ItemStack stack, List<ItemStack> stacks, Text title) {
        if (player.getWorld().isClient) return;
        var tempInv = new TempInventory(player, stack, stacks);
        var rows = tempInv.rowsToShow;
        player.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            public Text getDisplayName() {return title;}

            public Object getScreenOpeningData(ServerPlayerEntity player) {
                return new OpenScreenPayload(player.getId(), true, rows.toString());
            }
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new PlayerInvScreenHandler(syncId, tempInv, target, rows);
            }
        });
    }

    public static ItemStack paibei(int... n) {return new ItemStack(ModItems.GAIN_CARD, n.length > 0 ? n[0] : 1);}

    public static void closeGUI(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }

    public static RegistryEntry<Enchantment> getEntry(RegistryKey<Enchantment> key) {
        return DabaoSword.server.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key);
    }

    public static DamageSource damageSource(Entity source, RegistryKey<DamageType> type) {
        return new DamageSource(source.getWorld().getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(type), source);
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
                var entry = player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(type);
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

    public static List<LivingEntity> getSkillOwners(LivingEntity entity) {
        if (entity.getWorld().isClient) return List.of();
        return new ArrayList<>(Objects.requireNonNull(entity.getServer()).getPlayerManager().getPlayerList());
    }

    public static List<Skill> getSkillsMayUse(LivingEntity entity) {
        Predicate<ItemStack> p = s -> {
            if (!(s.getItem() instanceof ISkill)) return false;
            return s(s).lockOn() || !entity.hasStatusEffect(ModItems.TIEJI);
        };
        return getTrinketComponent(entity).map(c -> c.getEquipped(p).stream().map(Pair::getRight).map(Skill::new).toList()).orElse(Collections.emptyList());
    }

    public static int getResult(Trigger t, LivingEntity owner, LivingEntity triggered, ExData exData) {
        for (Skill skill : getSkillsMayUse(owner)) {
            for (var data : skill.data()) {
                if (!List.of(data.trigger()).contains(t) || !data.relation().test(owner, triggered, exData.source)) continue;
                int i = data.apply(owner, triggered, skill, exData);
                if (i > 0) return i;
            }
        }
        return 0;
    }

    public static String getTagValue(Entity entity, String name) {
        return entity.getCommandTags().stream().filter(s -> s.startsWith(name + "_")).findFirst().map(s -> s.split("_")[1]).orElse("");
    }
    public static int getTagCount(Entity entity, String name) {
        return entity.getCommandTags().stream().filter(s -> s.startsWith(name + "_")).findFirst().map(s -> Integer.parseInt(s.split("_")[1])).orElse(0);
    }

    public static boolean hasTag(Entity entity, String name) {
        return entity.getCommandTags().stream().anyMatch(s -> s.startsWith(name));
    }

    public static void removeTag(Entity entity, String name) {
        var each = entity.getCommandTags().iterator();
        while (each.hasNext()) { //仅移除第一个符合条件的标签
            if (each.next().startsWith(name)) {
                each.remove(); break;
            }
        }
    }

    public static void addTag(Entity entity, String name, int n) {
        removeTag(entity, name);
        entity.addCommandTag(name + "_" + n);
    }
    public static void addTag(Entity entity, String name, String suffix) {
        entity.addCommandTag(name + "_" + suffix);
    }

}
