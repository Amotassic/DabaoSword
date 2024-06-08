package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.card.GiftBoxItem;
import com.amotassic.dabaosword.item.equipment.*;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    //摸牌
    public static final Item GAIN_CARD = register("gain_card",new GainCardItem(new Item.Settings()));
    //牌堆
    public static final Item CARD_PILE = register("card_pile",new EquipmentItem(new Item.Settings().maxCount(1)));

    // 古锭刀
    public static final Item GUDINGDAO = register("gudingdao", new GudingdaoItem(new Item.Settings()));
    public static final Item GUDING_WEAPON = register("guding_dao", new EquipmentItem(new Item.Settings().maxCount(1)));
    //未锻造的古锭刀
    public static final Item INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new Item.Settings().maxCount(1)));
    //古锭
    public static final Item GUDING_ITEM = register("guding", new Item(new Item.Settings()));
    //方天画戟
    public static final Item FANGTIAN = register("fangtian", new FangtianWeapon(new Item.Settings().maxCount(1)));
    //寒冰剑
    public static final Item HANBING = register("hanbing", new EquipmentItem(new Item.Settings().maxCount(1)));
    //青釭剑
    public static final Item QINGGANG = register("qinggang", new EquipmentItem(new Item.Settings().maxCount(1)));
    //青龙偃月刀
    public static final Item QINGLONG = register("qinglong", new EquipmentItem(new Item.Settings().maxCount(1)));
    //八卦阵
    public static final Item BAGUA = register("bagua", new EquipmentItem(new Item.Settings().maxCount(1)));
    //白银狮子
    public static final Item BAIYIN = register("baiyin", new EquipmentItem(new Item.Settings().maxCount(1)));
    //寿衣
    public static final Item RATTAN_ARMOR = register("rattan_armor", new RattanArmor(new Item.Settings().maxCount(1)));
    //-1马
    public static final Item CHITU = register("chitu", new EquipmentItem(new Item.Settings().maxCount(1)));
    //+1马
    public static final Item DILU = register("dilu", new EquipmentItem(new Item.Settings().maxCount(1)));

    //杀
    public static final Item SHA = register("sha", new CardItem(new Item.Settings()));
    public static final Item FIRE_SHA = register("fire_sha", new CardItem(new Item.Settings()));
    public static final Item THUNDER_SHA = register("thunder_sha", new CardItem(new Item.Settings()));
    //闪
    public static final Item SHAN = register("shan", new ShanItem(new Item.Settings()));
    //桃
    public static final Item PEACH = register("peach", new PeachItem(new Item.Settings()));
    //酒
    public static final Item JIU = register("jiu", new JiuItem(new Item.Settings()));

    //兵粮寸断
    public static final Item BINGLIANG_ITEM = register("bingliang",new BingliangItem(new Item.Settings()));
    //乐不思蜀
    public static final Item TOO_HAPPY_ITEM = register("too_happy", new TooHappyItem(new Item.Settings()));
    //过河拆桥
    public static final Item DISCARD = register("discard", new DiscardItem(new Item.Settings()));
    //火攻
    public static final Item FIRE_ATTACK = register("huogong", new FireAttackItem(new Item.Settings()));
    //借刀杀人
    public static final Item JIEDAO = register("jiedao", new JiedaoItem(new Item.Settings()));
    //决斗
    public static final Item JUEDOU = register("juedou",new JuedouItem(new Item.Settings()));
    //南蛮入侵
    public static final Item NANMAN = register("nanman", new NanmanItem(new Item.Settings()));
    //顺手牵羊
    public static final Item STEAL = register("steal", new StealItem(new Item.Settings()));
    //桃园结义
    public static final Item TAOYUAN = register("taoyuan", new TaoyuanItem(new Item.Settings()));
    //铁锁连环
    public static final Item TIESUO = register("tiesuo",new TiesuoItem(new Item.Settings()));
    //万箭齐发
    public static final Item ARROW_RAIN = register("arrow_rain", new ArrowRainItem(new Item.Settings().maxDamage(50)));
    public static final Item WANJIAN = register("wanjian", new WanjianItem(new Item.Settings()));
    //无懈可击
    public static final Item WUXIE = register("wuxie", new CardItem(new Item.Settings()));
    //无中生有
    public static final Item WUZHONG = register("wuzhong", new GainCardItem(new Item.Settings()));
    //礼盒
    public static final Item GIFTBOX = register("gift_box", new GiftBoxItem(new Item.Settings()));
    //BB机
    public static final Item BBJI = register("bbji", new BBjiItem(new Item.Settings().maxDamage(250)));

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("dabaosword", "item_group"), DABAOSWORD_GROUP);
    }

    private static StatusEffect register(String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", id), entry);
    }//状态效果注册
    //兵粮寸断效果
    public static final StatusEffect BINGLIANG = register("bingliang",
            new BingliangEffect(StatusEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,"22653B89-116E-49DC-9B6B-9971489B5BE5",-4, EntityAttributeModifier.Operation.ADDITION));
    //乐不思蜀效果
    public static final StatusEffect TOO_HAPPY = register("too_happy",
            new TooHappyEffect(StatusEffectCategory.HARMFUL, 0xF73C0A).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,"7107DE5E-7CE8-4030-940E-514C1F160890",-10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    //触及距离增加
    public static final StatusEffect REACH = register("reach", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(ReachEntityAttributes.REACH,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION));
    //近战防御范围增加
    public static final StatusEffect DEFEND = register("defend", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final StatusEffect DEFENDED = register("defended",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"6656ba40-7a9c-a584-3c63-1e1e0e655446",-1.0, EntityAttributeModifier.Operation.ADDITION));
    //冷却状态效果
    public static final StatusEffect COOLDOWN = register(
            "cooldown", new CooldownEffect(StatusEffectCategory.NEUTRAL, 0xFFFFFF));
    public static final StatusEffect COOLDOWN2 = register(
            "cooldown2", new Cooldown2Effect(StatusEffectCategory.NEUTRAL, 0xFFFFFF));
    //无敌效果
    public static final StatusEffect INVULNERABLE = register(
            "invulnerable", new InvulnerableEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF));
    //下落攻击效果
    public static final StatusEffect FALLING_ATTACK = register(
            "falling_attack", new FallingEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    //翻面效果
    public static final StatusEffect TURNOVER = register(
            "turn_over", new TurnOverEffect(StatusEffectCategory.HARMFUL, 0x07050F));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }

    public static void registerItems() {}

    //物品组添加
    public static final ItemGroup DABAOSWORD_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GUDINGDAO))
            .displayName(Text.translatable("itemGroup.dabaosword.item_group"))
            .entries((context, entries) -> {
                entries.add(GUDING_WEAPON);
                entries.add(FANGTIAN);
                entries.add(HANBING);
                entries.add(QINGGANG);
                entries.add(QINGLONG);
                entries.add(BAGUA);
                entries.add(BAIYIN);
                entries.add(RATTAN_ARMOR);
                entries.add(GAIN_CARD);
                entries.add(CARD_PILE);
                entries.add(SHA);
                entries.add(FIRE_SHA);
                entries.add(THUNDER_SHA);
                entries.add(SHAN);
                entries.add(PEACH);
                entries.add(JIU);
                entries.add(BINGLIANG_ITEM);
                entries.add(TOO_HAPPY_ITEM);
                entries.add(DISCARD);
                entries.add(FIRE_ATTACK);
                entries.add(JIEDAO);
                entries.add(JUEDOU);
                entries.add(NANMAN);
                entries.add(STEAL);
                entries.add(TAOYUAN);
                entries.add(TIESUO);
                entries.add(WANJIAN);
                entries.add(WUXIE);
                entries.add(WUZHONG);
                entries.add(CHITU);
                entries.add(DILU);
                //魏
                entries.add(SkillCards.DUANLIANG);
                entries.add(SkillCards.FANGZHU);
                entries.add(SkillCards.XINGSHANG);
                entries.add(SkillCards.GANGLIE);
                entries.add(SkillCards.GONGAO);
                entries.add(SkillCards.JUEQING);
                entries.add(SkillCards.LUOSHEN);
                entries.add(SkillCards.QINGGUO);
                entries.add(SkillCards.LUOYI);
                entries.add(SkillCards.QICE);
                entries.add(SkillCards.QUANJI);
                entries.add(SkillCards.YIJI);
                //蜀
                entries.add(SkillCards.BENXI);
                entries.add(SkillCards.HUOJI);
                entries.add(SkillCards.KANPO);
                entries.add(SkillCards.JIZHI);
                entries.add(SkillCards.KUANGGU);
                entries.add(SkillCards.LIEGONG);
                //吴
                entries.add(SkillCards.GUOSE);
                entries.add(SkillCards.LIULI);
                entries.add(SkillCards.KUROU);
                entries.add(SkillCards.POJUN);
                entries.add(SkillCards.QIXI);
                //群
                entries.add(SkillCards.LEIJI);
                entries.add(SkillCards.LUANJI);
                entries.add(SkillCards.TAOLUAN);
                entries.add(SkillCards.MASHU);
                entries.add(SkillCards.FEIYING);

                entries.add(GIFTBOX);
                entries.add(BBJI);
            }).build();
}
