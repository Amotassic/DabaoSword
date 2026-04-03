package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.DabaoSword;
import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.card.Suit;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.api.skill.Trigger;
import com.amotassic.dabaosword.data.CardSuitAndRank;
import com.amotassic.dabaosword.event.PVPGameEvents;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.card.Sha;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.api.TrinketInventory;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
    public static Predicate<ItemStack> p(Item item) {return s -> s.is(item);}
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

    @SafeVarargs
    public static <T> List<T> toList(T... t) {return new ArrayList<>(Arrays.asList(t));}

    /**判断是否有某个饰品*/
    public static boolean hasTrinket(Item item, LivingEntity entity) {return isEquipped(entity, p(item));}
    public static boolean isEquipped(LivingEntity entity, Predicate<ItemStack> p) {
        return getTrinketComponent(entity).map(c -> c.isEquipped(p)).orElse(false);
    }

    public static ItemStack trinketItem(Item item, LivingEntity entity) {
        return getTrinketComponent(entity).map(c -> c.getEquipped(item).stream().map(Tuple::getB).findFirst().orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    /**获取该实体的所有饰品，输出为ItemStack列表*/
    public static List<ItemStack> allTrinkets(LivingEntity entity) {
        return getTrinketComponent(entity).map(c -> c.getAllEquipped().stream().map(Tuple::getB).toList()).orElse(Collections.emptyList());
    }
    public static List<Tuple<TrinketInventory, Integer>> trinketsWithSlots(LivingEntity entity) {
        return trinketsWithSlots(entity, s -> true);
    }
    public static List<Tuple<TrinketInventory, Integer>> trinketsWithSlots(LivingEntity entity, Predicate<ItemStack> filter) {
        List<Tuple<TrinketInventory, Integer>> pairs = new ArrayList<>();
        getTrinketComponent(entity).ifPresent(c -> c.getInventory().values().forEach(group -> group.values().forEach(inv -> {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (filter.test(inv.getItem(i))) pairs.add(new Tuple<>(inv, i));
            }
        })));
        return pairs;
    }

    public static void replaceTrinketSlot(List<Tuple<TrinketInventory, Integer>> pairs, int slot) {
        if (slot < 1 || slot >= pairs.size()) return;
        List<ItemStack> copys = new ArrayList<>(pairs.stream().map(p -> p.getA().getItem(p.getB()).copy()).toList());
        ItemStack stack = copys.get(slot).copy();
        for (int i = slot; i > 0; i--) {
            copys.set(i, copys.get(i - 1));
        }
        copys.set(0, stack);
        for (int i = 0; i < pairs.size(); i++) {
            pairs.get(i).getA().setItem(pairs.get(i).getB(), copys.get(i));
        }
    }

    public static CardPileInventory getCardPack(Player player) {
        if (player instanceof ServerPlayer sp) return PVPGameEvents.PLAYER_CARD_PACKS.getOrDefault(sp, new CardPileInventory(player));
        return new CardPileInventory(player);
    }

    public static boolean shouldReachLong(LivingEntity entity) {
        for (var hand : InteractionHand.values()) {
            if (entity.getItemInHand(hand).getItem() instanceof CardItem c && c.rangedUse()) return true;
        }
        return false;
    }

    /**判断牌堆和背包中是否有符合条件的卡牌*/
    public static boolean hasCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getCard(entity, predicate).isEmpty();
    }
    /**获取牌堆或背包中的一张符合条件的卡牌*/
    public static ItemStack getCard(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof Player player) {
            for (ItemStack card : getCardPack(player).cards) {
                if (predicate.test(card)) return card;
            }
        }
        return getItem(entity, predicate);
    }

    /**判断生物是否有某个物品*/
    public static boolean hasItem(LivingEntity entity, Predicate<ItemStack> predicate) {
        return !getItem(entity, predicate).isEmpty();
    }
    /**获取玩家背包中第一个符合条件的物品，或者生物的符合条件的主副手物品*/
    public static ItemStack getItem(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity instanceof Player player) {
            for (var stack : player.getInventory().getNonEquipmentItems()) if (predicate.test(stack)) return stack;
        } else if (predicate.test(entity.getMainHandItem())) return entity.getMainHandItem();
        if (predicate.test(entity.getOffhandItem())) return entity.getOffhandItem();
        return ItemStack.EMPTY;
    }

    /**播放语音*/
    public static void voice(LivingEntity entity, SoundEvent sound, float... volume) {
        if (entity.level() instanceof ServerLevel world) {
            float v = volume.length > 0 ? volume[0] : 2;
            world.playSeededSound(null, entity, Holder.direct(sound), SoundSource.PLAYERS, v, 1.0F, RandomSource.create().nextLong());
        }
    }
    public static void voice(LivingEntity entity, Item item, float... volume) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (item instanceof CardItem) path = path.replace("card/", "");
        else if (item instanceof SkillItem) path = path.replace("skill/", "");
        voice(entity, path, volume);
    }
    public static void voice(LivingEntity entity, ItemStack stack, float... volume) {
        voice(entity, stack.getItem(), volume);
    }
    public static void voice(LivingEntity entity, String name, float... volume) {
        voice(entity, getSound("dabaosword", name), volume);
    }
    public static SoundEvent getSound(String namespace, String path) {
        return Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(namespace, path))).value();
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

    public static List<ItemStack> getArmorItems(LivingEntity entity) {
        List<ItemStack> items = new ArrayList<>();
        items.add(entity.getItemBySlot(EquipmentSlot.FEET));
        items.add(entity.getItemBySlot(EquipmentSlot.LEGS));
        items.add(entity.getItemBySlot(EquipmentSlot.CHEST));
        items.add(entity.getItemBySlot(EquipmentSlot.HEAD));
        return items;
    }

    /**将一个生物的所有符合条件的物品整理成一个list
     * @param main 如果是玩家，包括玩家的物品栏和副手物品，否则只包括生物的主副手物品*/
    public static List<ItemStack> getItems(LivingEntity entity, Predicate<ItemStack> p, boolean main, boolean armor, boolean trinket, boolean pile) {
        List<ItemStack> items = new ArrayList<>();
        //如果是玩家则把牌堆中的物品添加到待选物品中
        if (pile && entity instanceof Player player) for (var stack : getCardPack(player).cards) if (p.test(stack)) items.add(stack);
        if (main) { //如果是玩家则把背包和副手的物品添加到待选物品中，否则只添加主副手物品
            if (entity instanceof Player player) {
                for (var stack : player.getInventory().getNonEquipmentItems()) if (p.test(stack)) items.add(stack);
            } else if (p.test(entity.getMainHandItem())) items.add(entity.getMainHandItem());
            if (p.test(entity.getOffhandItem())) items.add(entity.getOffhandItem());
        }
        if (armor) for (var stack : getArmorItems(entity)) if (p.test(stack)) items.add(stack);
        if (trinket) for (var stack : allTrinkets(entity)) if (p.test(stack)) items.add(stack);
        return items;
    }

    public static void draw(LivingEntity entity, int... count) {
        int num = count.length > 0 ? count[0] : 1;
        for (int n = 0; n < num; n++) {
            if (entity.hasEffect(ModItems.BINGLIANG)) {
                int amplifier = Objects.requireNonNull(entity.getEffect(ModItems.BINGLIANG)).getAmplifier();
                entity.removeEffect(ModItems.BINGLIANG);
                voice(entity, SoundEvents.VILLAGER_NO,1);
                if (amplifier != 0) {
                    entity.addEffect(new MobEffectInstance(ModItems.BINGLIANG, -1, amplifier - 1));
                } //如果有兵粮寸断效果就不摸牌，改为将debuff等级减一
            } else {
                give(entity, newCard());
                voice(entity, SoundEvents.EXPERIENCE_ORB_PICKUP,1);
            }
        }
    }

    public static ItemStack customLoot(LivingEntity entity, String path) {
        var key = ResourceKey.create(Registries.LOOT_TABLE, DabaoSword.id(path));
        AtomicReference<ItemStack> stack = new AtomicReference<>(ItemStack.EMPTY);
        entity.dropFromGiftLootTable(world(entity), key, (world, s) -> stack.set(s));
        return stack.get();
    }

    private static final List<ItemStack> CARD_PILE = new ArrayList<>();
    public static ItemStack newCard() {
        if (CARD_PILE.isEmpty()) {
            for (ItemStack stack : CardSuitAndRank.getAllCards()) CARD_PILE.add(stack.copy());
            Collections.shuffle(CARD_PILE);
            DabaoSword.LOGGER.info("Shuffled card pile");
        }
        return CARD_PILE.removeFirst();
    }
    public static ItemStack newCard(Item item) {return newCard(p(item));}
    public static ItemStack newCard(Predicate<ItemStack> predicate) {
        List<ItemStack> list = CardSuitAndRank.getAllCards().stream().filter(predicate).toList();
        if (list.isEmpty()) return ItemStack.EMPTY;
        return list.get(new Random().nextInt(list.size())).copy();
    }

    public static void give(LivingEntity entity, ItemStack stack, int... pickupDelay) {
        if (entity instanceof Player player) {
            ItemEntity item = player.drop(stack, false);
            if (item == null) return;
            item.setInvulnerable(true);
            int delay = pickupDelay.length > 0 ? pickupDelay[0] : 0;
            item.setPickUpDelay(delay);
            item.setTarget(player.getUUID());
            item.addTag("follow_owner");
            return;
        }
        if (entity.getMainHandItem().isEmpty()) entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
        else if (entity.getOffhandItem().isEmpty()) entity.setItemInHand(InteractionHand.OFF_HAND, stack);
    }

    public static @Nullable <T extends Entity> T getClosestEntity(Entity entity, Class<T> clazz, double boxLength, Predicate<T> predicate) {
        if (entity.level() instanceof ServerLevel world) {
            AABB box = new AABB(entity.getOnPos()).inflate(boxLength);
            List<T> entities = world.getEntitiesOfClass(clazz, box, predicate.and(e -> e != entity));
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
    public static CompoundTag getOrCreateNbt(ItemStack stack) {
        if (stack.isEmpty()) return new CompoundTag();
        var component = stack.get(DataComponents.CUSTOM_DATA);
        if (component == null) return new CompoundTag();
        return component.copyTag();
    }
    public static void setNbt(ItemStack stack, CompoundTag nbt) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static void openScreen(Player player, Component title, Function<ServerPlayer, Object> data, MenuConstructor factory) {
        player.openMenu(new ExtendedMenuProvider<>() {
            public @NonNull Component getDisplayName() {return title;}
            public Object getScreenOpeningData(@NonNull ServerPlayer player) {return data.apply(player);}
            public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
                return factory.createMenu(containerId, inventory, player);
            }
        });
    }

    public static void openFullInv(Player player, LivingEntity target, boolean editable) {
        if (player.level().isClientSide()) return;
        var payload = new SimplePayload(Integer.toString(target.getId()), String.valueOf(editable));
        openScreen(player, target.getDisplayName(), p -> payload,
                (syncId, inv, p) -> new FullInvScreenHandler(syncId, inv, payload));
    }

    public static void openInv(Player player, LivingEntity owner, Player target, Component title, ItemStack stack, boolean equip, boolean armor, int cards) {
        if (player.level().isClientSide()) return;
        var tempInv = new TempInventory(player, owner, stack, cards, equip, armor);
        var rows = tempInv.rowsToShow;
        var payload = new SimplePayload(Integer.toString(target.getId()), rows.toString());
        openScreen(player, title, p -> payload,
                (syncId, inv, p) -> new PlayerInvScreenHandler(syncId, tempInv, target, rows));
    }

    public static void openMenu(Player player, Player target, ItemStack stack, List<ItemStack> stacks, Component title) {
        if (player.level().isClientSide()) return;
        var tempInv = new TempInventory(player, stack, stacks);
        var rows = tempInv.rowsToShow;
        var payload = new SimplePayload(Integer.toString(target.getId()), rows.toString());
        openScreen(player, title, p -> payload,
                ((syncId, inv, p) -> new PlayerInvScreenHandler(syncId, tempInv, target, rows)));
    }

    public static ItemStack paibei(int... n) {return new ItemStack(ModItems.GAIN_CARD, n.length > 0 ? n[0] : 1);}

    public static void closeGUI(Player player) {
        player.addEffect(new MobEffectInstance(ModItems.COOLDOWN2, 1,2,false,false,false));
    }

    public static Holder<Enchantment> getEntry(ResourceKey<Enchantment> key, Entity... entity) {
        Entity e = entity.length > 0 ? entity[0] : null;
        Registry<Enchantment> enchantments;
        if (e == null) enchantments = DabaoSword.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        else enchantments = e.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return enchantments.get(key).orElse(null);
    }

    public static DamageSource damageSource(Entity source, ResourceKey<DamageType> type) {
        return new DamageSource(source.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type), source);
    }

    public static void writeDamage(DamageSource source, float amount, boolean returnShan, ItemStack stack) {
        ListTag list = new ListTag();
        CompoundTag compound = new CompoundTag();
        //noinspection OptionalGetWithoutIsPresent
        compound.putString("type", source.typeHolder().unwrapKey().get().identifier().toString());
        if (source.getDirectEntity() != null) compound.putInt("source", source.getDirectEntity().getId());
        if (source.getEntity() != null) compound.putInt("attacker", source.getEntity().getId());
        compound.putFloat("amount", amount);
        if (returnShan) compound.putString("returning", "dabaosword:card/shan");
        list.add(compound);
        var nbt = getOrCreateNbt(stack);
        nbt.put("DamageDodged", list);
        setNbt(stack, nbt);
    }
    //牌堆记录闪避伤害的方法
    public static Tuple<Tuple<DamageSource, Float>, ItemStack> getDamage(Player player) {
        if (player.level() instanceof ServerLevel world) {
            ItemStack stack = trinketItem(ModItems.CARD_PILE, player);
            var compound = getOrCreateNbt(stack);
            if (compound.contains("DamageDodged")) {
                var nbt = compound.getList("DamageDodged").orElseThrow().getCompound(0).orElseThrow();
                ResourceKey<DamageType> type = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse(nbt.getString("type").orElseThrow()));
                var entry = player.level().registryAccess().getOrThrow(Registries.DAMAGE_TYPE).value().getOrThrow(type);
                Entity source = world.getEntity(nbt.getInt("source").orElse(0));
                Entity attacker = world.getEntity(nbt.getInt("attacker").orElse(0));
                DamageSource damageSource = new DamageSource(entry, source, attacker);
                ItemStack returning = ItemStack.EMPTY;
                if (nbt.contains("returning")) returning = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(nbt.getString("returning").orElseThrow())));
                float amount = nbt.getFloat("amount").orElseThrow();
                return new Tuple<>(new Tuple<>(damageSource, amount), returning);
            }
        }
        return null;
    }

    public static ServerLevel world(Entity entity) {return (ServerLevel) entity.level();}

    /**一个用于简便执行多条服务器指令的方法*/
    public static void excuteServerCommand(Entity entity, String... commands) {
        if (entity.level() instanceof ServerLevel world) {
            var server = world.getServer();
            var dispatcher = server.getCommands().getDispatcher();
            var commandSource = entity.createCommandSourceStackForNameResolution(world).withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput();
            for (String command : commands) {
                if (command.startsWith("/")) command = command.substring(1);
                try {
                    var results = dispatcher.parse(command, commandSource);
                    dispatcher.execute(results);
                } catch (CommandSyntaxException e) {throw new RuntimeException(e);}
            }
        }
    }

    public static void title(ServerPlayer player, Component title) {
        player.connection.send(new ClientboundSetTitleTextPacket(title));
    }

    public static void subtitle(ServerPlayer player, Component sub) {
        player.connection.send(new ClientboundSetSubtitleTextPacket(sub));
    }

    public static List<LivingEntity> getSkillOwners(LivingEntity entity) {
        if (entity.level().isClientSide()) return List.of();
        return new ArrayList<>(Objects.requireNonNull(entity.level().getServer()).getPlayerList().getPlayers());
    }

    public static List<Skill> getSkillsMayUse(LivingEntity entity) {
        Predicate<ItemStack> p = s -> {
            if (!(s.getItem() instanceof ISkill)) return false;
            if (s.getItem() instanceof SkillItem && entity.entityTags().contains("duanchang")) return false;
            return s(s).lockOn() || !entity.hasEffect(ModItems.TIEJI);
        };
        return getTrinketComponent(entity).map(c -> c.getEquipped(p).stream().map(Tuple::getB).map(Skill::new).toList()).orElse(Collections.emptyList());
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
        return entity.entityTags().stream().filter(s -> s.startsWith(name + "_")).findFirst().map(s -> s.split("_")[1]).orElse("");
    }
    public static int getTagCount(Entity entity, String name) {
        return entity.entityTags().stream().filter(s -> s.startsWith(name + "_")).findFirst().map(s -> Integer.parseInt(s.split("_")[1])).orElse(0);
    }

    public static boolean hasTag(Entity entity, String name) {
        return entity.entityTags().stream().anyMatch(s -> s.startsWith(name));
    }

    public static void removeTag(Entity entity, String name) {
        var each = entity.entityTags().iterator();
        while (each.hasNext()) { //仅移除第一个符合条件的标签
            if (each.next().startsWith(name)) {
                each.remove(); break;
            }
        }
    }

    public static void addTag(Entity entity, String name, int n) {
        removeTag(entity, name);
        entity.addTag(name + "_" + n);
    }
    public static void addTag(Entity entity, String name, String suffix) {
        entity.addTag(name + "_" + suffix);
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

}
