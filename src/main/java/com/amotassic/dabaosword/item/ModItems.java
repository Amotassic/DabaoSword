package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.api.event.*;
import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.entity.ModEntity;
import com.amotassic.dabaosword.event.*;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.card.equipment.Armor;
import com.amotassic.dabaosword.item.card.equipment.Mount;
import com.amotassic.dabaosword.item.card.equipment.Weapon;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.item.tool.*;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.*;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class ModItems {
    public static final List<CardItem> CARDS = new ArrayList<>();
    //杀
    public static final CardItem
    SHA = registerCard("sha", Sha::new),
    FIRE_SHA = registerCard("fire_sha", Sha.Fire::new),
    THUNDER_SHA = registerCard("thunder_sha", Sha.Thunder::new),
    //闪
    SHAN = registerCard("shan", ShanItem::new),
    //桃
    PEACH = registerCard("peach", PeachItem::new),
    //酒
    JIU = registerCard("jiu", JiuItem::new),

    //兵粮寸断
    BINGLIANG_ITEM = registerCard("bingliang", BingliangItem::new),
    //乐不思蜀
    TOO_HAPPY_ITEM = registerCard("too_happy", TooHappyItem::new),
    //闪电
    SHANDIAN_ITEM = registerCard("shandian", ShandianItem::new),
    //过河拆桥
    DISCARD = registerCard("discard", DiscardItem::new),
    //火攻
    FIRE_ATTACK = registerCard("huogong", FireAttackItem::new),
    //借刀杀人
    JIEDAO = registerCard("jiedao", JiedaoItem::new),
    //决斗
    JUEDOU = registerCard("juedou", JuedouItem::new),
    //南蛮入侵
    NANMAN = registerCard("nanman", NanmanItem::new),
    //顺手牵羊
    STEAL = registerCard("steal", StealItem::new),
    //桃园结义
    TAOYUAN = registerCard("taoyuan", TaoyuanItem::new),
    //铁锁连环
    TIESUO = registerCard("tiesuo", TiesuoItem::new),
    //万箭齐发
    WANJIAN = registerCard("wanjian", WanjianItem::new),
    //五谷丰登
    WUGU = registerCard("wugu", WuguItem::new),
    //无懈可击
    WUXIE = registerCard("wuxie", CardItem.Armoury::new),
    //无中生有
    WUZHONG = registerCard("wuzhong", WuzhongItem::new),

    //雌雄双股剑
    CIXIONG = registerCard("cixiong", Weapon.Cixiong::new, 1),
    //方天画戟
    FANGTIAN = registerCard("fangtian", Weapon.Fangtian::new, 1),
    //贯石斧
    GUANSHI = registerCard("guanshi", Weapon.Guanshi::new, 1),
    // 古锭刀
    GUDING_WEAPON = registerCard("guding_dao", Weapon.Guding::new, 1),
    //寒冰剑
    HANBING = registerCard("hanbing", Weapon.Hanbing::new, 1),
    //麒麟弓
    QILIN = registerCard("qilin", Weapon.Qilin::new, 1),
    //青釭剑
    QINGGANG = registerCard("qinggang", Weapon.Qinggang::new, 1),
    //青龙偃月刀
    QINGLONG = registerCard("qinglong", Weapon.Qinglong::new, 1),
    //丈八蛇矛
    ZHANGBA = registerCard("zhangba", Weapon.Zhangba::new, 1),
    //诸葛连弩
    LIANNU = registerCard("liannu", Weapon.Liannu::new, 1),
    //朱雀羽扇
    ZHUQUE = registerCard("zhuque", Weapon.Zhuque::new, 1),
    //八卦阵
    BAGUA = registerCard("bagua", Armor.Bagua::new, 1),
    //白银狮子
    BAIYIN = registerCard("baiyin", Armor.Baiyin::new, 1),
    //仁王盾
    RENWANG = registerCard("renwang", Armor.Renwang::new, 1),
    //寿衣
    RATTAN_ARMOR = registerCard("rattan_armor", Armor.Rattan::new, 1),
    //-1马
    CHITU = registerCard("chitu", Mount.Attack::new, 1),
    //+1马
    DILU = registerCard("dilu", Mount.Defend::new, 1);

    public static final Item
    //摸牌
    GAIN_CARD = register("gain_card", GainCardItem::new),
    //牌堆
    CARD_PILE = register("card_pile", CardPile::new, 1),
    //礼盒
    GIFTBOX = register("gift_box", GiftBoxItem::new, new Item.Settings().rarity(Rarity.UNCOMMON)),
    GUDINGDAO = register("gudingdao", GudingdaoItem::new, new Item.Settings().maxDamage(999).rarity(Rarity.EPIC).sword(ToolMaterial.NETHERITE, 5, -2.4f)),
    ARROW_RAIN = register("arrow_rain", ArrowRainItem::new, new Item.Settings().maxDamage(50).rarity(Rarity.UNCOMMON)),
    WARM_WINE = register("warm_wine", WarmWineItem::new, new Item.Settings().rarity(Rarity.UNCOMMON)),
    //BB机
    BBJI = register("bbji", BBjiItem::new, new Item.Settings().maxDamage(250).rarity(Rarity.UNCOMMON)),
    //让我康康
    LET_ME_CC = register("let_me_cc", LetMeCCItem::new, 1),
    //阳光开朗的笑容
    SUNSHINE_SMILE = register("sunshine_smile", SunshineSmile::new, new Item.Settings().maxDamage(999).rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD).enchantable(25).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)),
    XUYOU_SPAWN_EGG = register("xuyou_spawn_egg", s -> new SpawnEggItem(ModEntity.XUYOU, s)),
    GUDING_ITEM = register("guding", Item::new),
    INCOMPLETE_GUDINGDAO = register("incomplete_gdd", Item::new, 1);
    public static final CardItem EMPTY_CARD = register("empty_card", CardItem.Empty::new);
    public static final SkillItem EMPTY_SKILL = register("empty_skill", SkillItem::new);

    public static <T extends Item> T register(String id, Function<Item.Settings, T> factory, int... maxCount) {
        Item.Settings settings = new Item.Settings();
        if (maxCount.length > 0) settings = settings.maxCount(maxCount[0]);
        return register(id, factory, settings);
    }
    /**将注册的卡牌添加到卡牌列表中，便于自动将物品添加到物品组*/
    public static CardItem registerCard(String name, Function<Item.Settings, CardItem> factory, int... maxCount) {
        CardItem card = register(name, factory, maxCount);
        CARDS.add(card);
        return card;
    }

    public static <T extends Item> T register(String id, Function<Item.Settings, T> factory, Item.Settings settings) {
        var key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("dabaosword", id));
        T item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        return Registry.register(Registries.ITEM, key, item);
    }

    public static final RegistryKey<ItemGroup> ZZRS = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("dabaosword", "zzrs"));

    private static void addToGroup(ItemGroup.DisplayContext context, ItemGroup.Entries entries) {
        var wrapper = context.lookup().getOrThrow(RegistryKeys.ENCHANTMENT);
        var entry = wrapper.getOptional(CRIT).orElse(null);
        ItemStack smile = new ItemStack(SUNSHINE_SMILE);
        if (entry != null) smile.addEnchantment(entry, 1);
        //添加所有卡牌
        CARDS.forEach(entries::add);
        entries.add(GAIN_CARD);
        entries.add(CARD_PILE);
        //添加所有技能
        SkillCards.SKILLS.forEach(entries::add);

        entries.add(GIFTBOX);
        entries.add(WARM_WINE);
        entries.add(BBJI);
        entries.add(LET_ME_CC);
        entries.add(smile);
        entries.add(XUYOU_SPAWN_EGG);
    }

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ZZRS,
                FabricItemGroup.builder().icon(() -> new ItemStack(SUNSHINE_SMILE))
                        .displayName(Text.translatable("itemGroup.dabaosword.zzrs"))
                        .entries(ModItems::addToGroup).build());

        ServerWorldEvents.LOAD.register(new PVPGameEvents());
        ServerTickEvents.START_SERVER_TICK.register(new PVPGameEvents());
        ServerTickEvents.START_WORLD_TICK.register(new PVPGameEvents());
        PVPGameTickCallback.EVENT.register(new PVPGameEvents());
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
        EntityHurtCallback.EVENT.register(new EntityHurtHandler());
        PlayerDeathCallback.EVENT.register(new PlayerEvents());
        PlayerRespawnCallback.EVENT.register(new PlayerEvents());
        EndEntityTick.LIVING_EVENT.register(new EntityTickEvents());
        EndEntityTick.PLAYER_EVENT.register(new EntityTickEvents());
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("dabaosword", id), statusEffect);
    }//状态效果注册
    //兵粮寸断效果
    public static final RegistryEntry<StatusEffect> BINGLIANG = register("bingliang",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, Identifier.of("bingliang"),-4, EntityAttributeModifier.Operation.ADD_VALUE)),
    //乐不思蜀效果
    TOO_HAPPY = register("too_happy", new TooHappyEffect().addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.of("too_happy"),-10, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
    //触及距离增加
    REACH = register("reach", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(EntityAttributes.BLOCK_INTERACTION_RANGE, Identifier.of("reach_block"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(EntityAttributes.ENTITY_INTERACTION_RANGE, Identifier.of("attack_range"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE)),
    //近战防御范围增加
    DEFEND = register("defend", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)),
    DEFENDED = register("defended", new CommonEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(EntityAttributes.ENTITY_INTERACTION_RANGE, Identifier.of("attack_range_lower"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE)),
    //冷却状态效果
    COOLDOWN = register("cooldown", new CooldownEffect()),
    COOLDOWN2 = register("cooldown2", new Cooldown2Effect()),
    //无敌效果
    INVULNERABLE = register("invulnerable", new CommonEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF)),
    //下落攻击效果
    FALLING_ATTACK = register("falling_attack", new FallingEffect()),
    //翻面效果
    TURNOVER = register("turn_over", new TurnOverEffect()),
    //铁骑效果
    TIEJI = register("tieji", new CommonEffect(StatusEffectCategory.HARMFUL, 0x07050F)),
    //闪电效果
    SHANDIAN = register("shandian", new ShandianEffect());

    //物品组件注册
    public static final ComponentType<Integer> TAGS = component("tags", builder -> builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
    public static final ComponentType<Integer> CD = component("cd", builder -> builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    private static <T> ComponentType<T> component(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("dabaosword",id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>(PlayerInvScreenHandler::new, SimplePayload.CODEC));

    public static final ScreenHandlerType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "full_inv", new ExtendedScreenHandlerType<>(FullInvScreenHandler::new, SimplePayload.CODEC));

    public static final ScreenHandlerType<PileScreenHandler> PILE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "card_pile", new ExtendedScreenHandlerType<>(PileScreenHandler::new, SimplePayload.CODEC));

    public static final RegistryKey<Enchantment> CRIT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("dabaosword:crit"));
}
