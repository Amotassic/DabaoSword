package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.DabaoSword;
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
import com.amotassic.dabaosword.util.ModTools;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;

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
    GIFTBOX = register("gift_box", GiftBoxItem::new, new Item.Properties().rarity(Rarity.UNCOMMON)),
    GUDINGDAO = register("gudingdao", GudingdaoItem::new, new Item.Properties().durability(999).rarity(Rarity.EPIC).sword(ToolMaterial.NETHERITE, 5, -2.4f)),
    ARROW_RAIN = register("arrow_rain", ArrowRainItem::new, new Item.Properties().durability(50).rarity(Rarity.UNCOMMON)),
    WARM_WINE = register("warm_wine", WarmWineItem::new, new Item.Properties().rarity(Rarity.UNCOMMON)),
    //BB机
    BBJI = register("bbji", BBjiItem::new, new Item.Properties().durability(250).rarity(Rarity.UNCOMMON)),
    //让我康康
    LET_ME_CC = register("let_me_cc", LetMeCCItem::new, 1),
    //阳光开朗的笑容
    SUNSHINE_SMILE = register("sunshine_smile", SunshineSmile::new, new Item.Properties().durability(999).rarity(Rarity.UNCOMMON).equippable(EquipmentSlot.HEAD).enchantable(25).component(DataComponents.UNBREAKABLE, Unit.INSTANCE)),
    XUYOU_SPAWN_EGG = register("xuyou_spawn_egg", SpawnEggItem::new, new Item.Properties().spawnEgg(ModEntity.XUYOU)),
    GUDING_ITEM = register("guding", Item::new),
    INCOMPLETE_GUDINGDAO = register("incomplete_gdd", Item::new, 1);
    public static final CardItem EMPTY_CARD = register("empty_card", CardItem.Empty::new);
    public static final SkillItem EMPTY_SKILL = register("empty_skill", SkillItem::new);

    public static <T extends Item> T register(String id, Function<Item.Properties, T> factory, int... maxCount) {
        Item.Properties settings = new Item.Properties();
        if (maxCount.length > 0) settings = settings.stacksTo(maxCount[0]);
        return register(id, factory, settings);
    }
    /**将注册的卡牌添加到卡牌列表中，便于自动将物品添加到物品组*/
    public static CardItem registerCard(String name, Function<Item.Properties, CardItem> factory, int... maxCount) {
        CardItem card = register("card/" + name, factory, maxCount);
        CARDS.add(card);
        return card;
    }

    public static <T extends Item> T register(String id, Function<Item.Properties, T> factory, Item.Properties settings) {
        var key = ResourceKey.create(Registries.ITEM, DabaoSword.id(id));
        T item = factory.apply(settings.setId(key));
        if (item instanceof BlockItem blockItem) blockItem.registerBlocks(Item.BY_BLOCK, item);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static final ResourceKey<CreativeModeTab> ZZRS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, DabaoSword.id("zzrs"));

    private static void addToGroup(CreativeModeTab.ItemDisplayParameters context, CreativeModeTab.Output entries) {
        //添加所有卡牌
        CARDS.forEach(entries::accept);
        entries.accept(GAIN_CARD);
        entries.accept(CARD_PILE);
        //添加所有技能
        SkillCards.SKILLS.forEach(entries::accept);

        entries.accept(GIFTBOX);
        entries.accept(WARM_WINE);
        entries.accept(BBJI);
        entries.accept(LET_ME_CC);
        addToGroupWithEnchant(entries, SUNSHINE_SMILE, CRIT, 1);
        entries.accept(XUYOU_SPAWN_EGG);
    }

    private static void addToGroupWithEnchant(CreativeModeTab.Output output, Item item, ResourceKey<Enchantment> key, int level) {
        ItemStack stack = new ItemStack(item);
        var player = Minecraft.getInstance().player;
        if (player != null) stack.enchant(ModTools.getEntry(key, player), level);
        output.accept(stack);
    }

    //注册部分
    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ZZRS,
                FabricCreativeModeTab.builder().icon(() -> new ItemStack(SUNSHINE_SMILE))
                        .title(Component.translatable("itemGroup.dabaosword.zzrs"))
                        .displayItems(ModItems::addToGroup).build());

        ServerLevelEvents.LOAD.register(new PVPGameEvents());
        ServerTickEvents.START_SERVER_TICK.register(new PVPGameEvents());
        ServerTickEvents.START_LEVEL_TICK.register(new PVPGameEvents());
        PVPGameTickCallback.EVENT.register(new PVPGameEvents());
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
        EntityHurtCallback.EVENT.register(new EntityHurtHandler());
        PlayerDeathCallback.EVENT.register(new PlayerEvents());
        PlayerRespawnCallback.EVENT.register(new PlayerEvents());
        EndEntityTick.LIVING_EVENT.register(new EntityTickEvents());
        EndEntityTick.PLAYER_EVENT.register(new EntityTickEvents());
    }

    private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, DabaoSword.id(id), statusEffect);
    }//状态效果注册
    //兵粮寸断效果
    public static final Holder<MobEffect> BINGLIANG = register("bingliang",
            new CommonEffect(MobEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.withDefaultNamespace("bingliang"), -4, AttributeModifier.Operation.ADD_VALUE)),
    //乐不思蜀效果
    TOO_HAPPY = register("too_happy", new TooHappyEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.withDefaultNamespace("too_happy"),-10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
    //触及距离增加
    REACH = register("reach", new CommonEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, Identifier.withDefaultNamespace("reach_block"), 1.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, Identifier.withDefaultNamespace("attack_range"), 1.0, AttributeModifier.Operation.ADD_VALUE)),
    //近战防御范围增加
    DEFEND = register("defend", new CommonEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF)),
    DEFENDED = register("defended", new CommonEffect(MobEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, Identifier.withDefaultNamespace("attack_range_lower"), -1.0, AttributeModifier.Operation.ADD_VALUE)),
    //冷却状态效果
    COOLDOWN = register("cooldown", new CooldownEffect()),
    COOLDOWN2 = register("cooldown2", new Cooldown2Effect()),
    //无敌效果
    INVULNERABLE = register("invulnerable", new CommonEffect(MobEffectCategory.BENEFICIAL,0x35F5DF)),
    //下落攻击效果
    FALLING_ATTACK = register("falling_attack", new FallingEffect()),
    //翻面效果
    TURNOVER = register("turn_over", new TurnOverEffect()),
    //铁骑效果
    TIEJI = register("tieji", new CommonEffect(MobEffectCategory.HARMFUL, 0x07050F)),
    //闪电效果
    SHANDIAN = register("shandian", new ShandianEffect());

    //物品组件注册
    public static final DataComponentType<Integer> TAGS = component("tags", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Integer> CD = component("cd", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    private static <T> DataComponentType<T> component(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DabaoSword.id(id), (builderOperator.apply(DataComponentType.builder())).build());
    }

    public static final MenuType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(BuiltInRegistries.MENU, "player_inv", new ExtendedMenuType<>(PlayerInvScreenHandler::new, SimplePayload.CODEC));

    public static final MenuType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(BuiltInRegistries.MENU, "full_inv", new ExtendedMenuType<>(FullInvScreenHandler::new, SimplePayload.CODEC));

    public static final MenuType<PileScreenHandler> PILE_SCREEN_HANDLER = Registry.register(BuiltInRegistries.MENU, "card_pile", new ExtendedMenuType<>(PileScreenHandler::new, SimplePayload.CODEC));

    public static final ResourceKey<Enchantment> CRIT = ResourceKey.create(Registries.ENCHANTMENT, DabaoSword.id("crit"));
}
