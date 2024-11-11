package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.api.CardPileInventory;
import com.amotassic.dabaosword.api.event.*;
import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.event.*;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.equipment.*;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.network.ActiveSkillPayload;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class ModItems {
    //杀
    public static final Item SHA = register("sha", new CardItem.Sha());
    public static final Item FIRE_SHA = register("fire_sha", new CardItem.Sha());
    public static final Item THUNDER_SHA = register("thunder_sha", new CardItem.Sha());
    //闪
    public static final Item SHAN = register("shan", new ShanItem());
    //桃
    public static final Item PEACH = register("peach", new PeachItem());
    //酒
    public static final Item JIU = register("jiu", new JiuItem());

    //兵粮寸断
    public static final Item BINGLIANG_ITEM = register("bingliang",new BingliangItem());
    //乐不思蜀
    public static final Item TOO_HAPPY_ITEM = register("too_happy", new TooHappyItem());
    //闪电
    public static final Item SHANDIAN_ITEM = register("shandian", new ShandianItem());
    //过河拆桥
    public static final Item DISCARD = register("discard", new DiscardItem());
    //火攻
    public static final Item FIRE_ATTACK = register("huogong", new FireAttackItem());
    //借刀杀人
    public static final Item JIEDAO = register("jiedao", new JiedaoItem());
    //决斗
    public static final Item JUEDOU = register("juedou",new JuedouItem());
    //南蛮入侵
    public static final Item NANMAN = register("nanman", new NanmanItem());
    //顺手牵羊
    public static final Item STEAL = register("steal", new StealItem());
    //桃园结义
    public static final Item TAOYUAN = register("taoyuan", new TaoyuanItem());
    //铁锁连环
    public static final Item TIESUO = register("tiesuo",new TiesuoItem());
    //万箭齐发
    public static final Item WANJIAN = register("wanjian", new WanjianItem());
    //五谷丰登
    public static final Item WUGU = register("wugu", new WuguItem());
    //无懈可击
    public static final Item WUXIE = register("wuxie", new CardItem());
    //无中生有
    public static final Item WUZHONG = register("wuzhong", new CardItem.Wuzhong());

    //方天画戟
    public static final Item FANGTIAN = register("fangtian", new Equipment.FangtianWeapon());
    // 古锭刀
    public static final Item GUDING_WEAPON = register("guding_dao", new Equipment.GudingWeapon());
    //寒冰剑
    public static final Item HANBING = register("hanbing", new Equipment.HanbingWeapon());
    //青釭剑
    public static final Item QINGGANG = register("qinggang", new Equipment.QinggangWeapon());
    //青龙偃月刀
    public static final Item QINGLONG = register("qinglong", new Equipment.QinglongWeapon());
    //丈八蛇矛
    public static final Item ZHANGBA = register("zhangba", new Equipment.ZhangbaWeapon());
    //八卦阵
    public static final Item BAGUA = register("bagua", new Equipment.BaguaArmor());
    //白银狮子
    public static final Item BAIYIN = register("baiyin", new Equipment.BaiyinArmor());
    //仁王盾
    public static final Item RENWANG = register("renwang", new Equipment.RenwangArmor());
    //寿衣
    public static final Item RATTAN_ARMOR = register("rattan_armor", new Equipment.RattanArmor());
    //-1马
    public static final Item CHITU = register("chitu", new Equipment());
    //+1马
    public static final Item DILU = register("dilu", new Equipment());

    //摸牌
    public static final Item GAIN_CARD = register("gain_card", new GainCardItem());
    //牌堆
    public static final Item CARD_PILE = register("card_pile", new CardPile());
    //礼盒
    public static final Item GIFTBOX = register("gift_box", new GiftBoxItem());
    public static final Item GUDINGDAO = register("gudingdao", new GudingdaoItem());
    public static final Item ARROW_RAIN = register("arrow_rain", new ArrowRainItem());
    //BB机
    public static final Item BBJI = register("bbji", new BBjiItem());
    //让我康康
    public static final Item LET_ME_CC = register("let_me_cc", new LetMeCCItem());
    //阳光开朗的笑容
    public static final Item SUNSHINE_SMILE = register("sunshine_smile", new SunshineSmile());
    public static final Item YES = register("yes", new Item(new Item.Settings()));
    public static final Item NO = register("no", new Item(new Item.Settings()));
    @SuppressWarnings("unused")
    public static final Item GUDING_ITEM = register("guding", new Item(new Item.Settings()));
    @SuppressWarnings("unused")
    public static final Item INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new Item.Settings().maxCount(1)));

    private static Item register(String id,Item item){
        return Registry.register(Registries.ITEM, Identifier.of("dabaosword",id),item);
    }

    public static final RegistryKey<ItemGroup> ZZRS = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("dabaosword", "item_group"));
    @SuppressWarnings("unused")
    public static ItemGroup ZZRS_GROUP = Registry.register(Registries.ITEM_GROUP, ZZRS.getRegistry(),
            FabricItemGroup.builder().icon(() -> new ItemStack(SUNSHINE_SMILE))
                    .displayName(Text.translatable("itemGroup.dabaosword.item_group"))
                    .entries((context, entries) -> {
                        entries.add(SHA);
                        entries.add(FIRE_SHA);
                        entries.add(THUNDER_SHA);
                        entries.add(SHAN);
                        entries.add(PEACH);
                        entries.add(JIU);
                        entries.add(BINGLIANG_ITEM);
                        entries.add(TOO_HAPPY_ITEM);
                        entries.add(SHANDIAN_ITEM);
                        entries.add(DISCARD);
                        entries.add(FIRE_ATTACK);
                        entries.add(JIEDAO);
                        entries.add(JUEDOU);
                        entries.add(NANMAN);
                        entries.add(STEAL);
                        entries.add(TAOYUAN);
                        entries.add(TIESUO);
                        entries.add(WANJIAN);
                        entries.add(WUGU);
                        entries.add(WUXIE);
                        entries.add(WUZHONG);

                        entries.add(FANGTIAN);
                        entries.add(GUDING_WEAPON);
                        entries.add(HANBING);
                        entries.add(QINGGANG);
                        entries.add(QINGLONG);
                        entries.add(ZHANGBA);
                        entries.add(BAGUA);
                        entries.add(BAIYIN);
                        entries.add(RENWANG);
                        entries.add(RATTAN_ARMOR);
                        entries.add(CHITU);
                        entries.add(DILU);
                        entries.add(GAIN_CARD);
                        entries.add(CARD_PILE);
                        //魏
                        entries.add(SkillCards.DUANLIANG);
                        entries.add(SkillCards.FANGZHU);
                        entries.add(SkillCards.XINGSHANG);
                        entries.add(SkillCards.GANGLIE);
                        entries.add(SkillCards.GONGAO);
                        entries.add(SkillCards.JIANXIONG);
                        entries.add(SkillCards.JUEQING);
                        entries.add(SkillCards.LUOSHEN);
                        entries.add(SkillCards.QINGGUO);
                        entries.add(SkillCards.LUOYI);
                        entries.add(SkillCards.QICE);
                        entries.add(SkillCards.QUANJI);
                        entries.add(SkillCards.SHANZHUAN);
                        entries.add(SkillCards.SHENSU);
                        entries.add(SkillCards.YIJI);
                        //蜀
                        entries.add(SkillCards.BENXI);
                        entries.add(SkillCards.HUOJI);
                        entries.add(SkillCards.KANPO);
                        entries.add(SkillCards.JIZHI);
                        entries.add(SkillCards.KUANGGU);
                        entries.add(SkillCards.LIEGONG);
                        entries.add(SkillCards.LONGDAN);
                        entries.add(SkillCards.RENDE);
                        entries.add(SkillCards.TIEJI);
                        entries.add(SkillCards.WUSHENG);
                        //吴
                        entries.add(SkillCards.BUQU);
                        entries.add(SkillCards.GONGXIN);
                        entries.add(SkillCards.GUOSE);
                        entries.add(SkillCards.LIANYING);
                        entries.add(SkillCards.LIULI);
                        entries.add(SkillCards.KUROU);
                        entries.add(SkillCards.POJUN);
                        entries.add(SkillCards.QIXI);
                        entries.add(SkillCards.XIAOJI);
                        entries.add(SkillCards.YINGZI);
                        entries.add(SkillCards.ZHIHENG);
                        entries.add(SkillCards.ZHIJIAN);
                        //群
                        entries.add(SkillCards.JIZHAN);
                        entries.add(SkillCards.LEIJI);
                        entries.add(SkillCards.LUANJI);
                        entries.add(SkillCards.TAOLUAN);
                        entries.add(SkillCards.WEIMU);
                        entries.add(SkillCards.MASHU);
                        entries.add(SkillCards.FEIYING);

                        entries.add(GIFTBOX);
                        entries.add(BBJI);
                        entries.add(LET_ME_CC);
                        entries.add(SUNSHINE_SMILE);
                    }).build());

    //注册部分
    public static void register() {
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
        EntityHurtCallback.EVENT.register(new EntityHurtHandler());
        PlayerConnectCallback.EVENT.register(new PlayerEvents());
        PlayerDeathCallback.EVENT.register(new PlayerEvents());
        PlayerRespawnCallback.EVENT.register(new PlayerEvents());
        CardCBs.USE_PRE.register(new CardEvents());
        CardCBs.USE_POST.register(new CardEvents());
        CardCBs.DISCARD.register(new CardEvents());
        CardCBs.MOVE.register(new CardEvents());
        EndEntityTick.LIVING_EVENT.register(new EntityTickEvents());
        EndEntityTick.PLAYER_EVENT.register(new EntityTickEvents());
        CardCBs.CAN_HURT_BY_CARD.register(new EntityHurtHandler());
        CardCBs.HURT_BY_CARD.register(new EntityHurtHandler());
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("dabaosword", id), statusEffect);
    }//状态效果注册
    //兵粮寸断效果
    public static final RegistryEntry<StatusEffect> BINGLIANG = register("bingliang",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, Identifier.of("bingliang"),-4, EntityAttributeModifier.Operation.ADD_VALUE));
    //乐不思蜀效果
    public static final RegistryEntry<StatusEffect> TOO_HAPPY = register("too_happy",
            new TooHappyEffect().addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of("too_happy"),-10, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    //触及距离增加
    public static final RegistryEntry<StatusEffect> REACH = register("reach", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, Identifier.of("reach_block"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, Identifier.of("attack_range"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE));
    //近战防御范围增加
    public static final RegistryEntry<StatusEffect> DEFEND =
            register("defend", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryEntry<StatusEffect> DEFENDED = register("defended",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, Identifier.of("attack_range_lower"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE));
    //冷却状态效果
    public static final RegistryEntry<StatusEffect> COOLDOWN = register("cooldown", new CooldownEffect());
    public static final RegistryEntry<StatusEffect> COOLDOWN2 = register("cooldown2", new Cooldown2Effect());
    //无敌效果
    public static final RegistryEntry<StatusEffect> INVULNERABLE = register("invulnerable", new InvulnerableEffect());
    //下落攻击效果
    public static final RegistryEntry<StatusEffect> FALLING_ATTACK = register("falling_attack", new FallingEffect());
    //翻面效果
    public static final RegistryEntry<StatusEffect> TURNOVER = register("turn_over", new TurnOverEffect());
    //铁骑效果
    public static final RegistryEntry<StatusEffect> TIEJI =
            register("tieji", new CommonEffect(StatusEffectCategory.HARMFUL, 0x07050F));
    //闪电效果
    public static final RegistryEntry<StatusEffect> SHANDIAN = register("shandian", new ShandianEffect());

    //物品组件注册
    public static final ComponentType<Integer> TAGS = register("tags", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
    public static final ComponentType<Integer> CD = register("cd", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("dabaosword",id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static final ScreenHandlerType<SimpleMenuHandler> SIMPLE_MENU_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "simple_menu", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new SimpleMenuHandler(syncId, new SimpleInventory(20), (PlayerEntity) inv.player.getWorld().getEntityById(data.id())), ActiveSkillPayload.CODEC));

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new PlayerInvScreenHandler(syncId, new SimpleInventory(60), (PlayerEntity) inv.player.getWorld().getEntityById(data.id())), ActiveSkillPayload.CODEC));

    public static final ScreenHandlerType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "full_inv", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new FullInvScreenHandler(syncId, inv, new SimpleInventory(64), (LivingEntity) inv.player.getWorld().getEntityById(data.id())), ActiveSkillPayload.CODEC));

    public static final ScreenHandlerType<PileScreenHandler> PILE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "card_pile", new ExtendedScreenHandlerType<>((syncId, inv, data) -> new PileScreenHandler(syncId, inv, new CardPileInventory(inv.player)), ActiveSkillPayload.CODEC));
}
