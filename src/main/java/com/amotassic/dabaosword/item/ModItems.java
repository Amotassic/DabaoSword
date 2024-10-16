package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.api.event.*;
import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.event.*;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.equipment.*;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    //摸牌
    public static final Item GAIN_CARD = register("gain_card",new GainCardItem(new Item.Settings()));
    //牌堆
    public static final Item CARD_PILE = register("card_pile",new CardPile(new Item.Settings().maxCount(1)));

    // 古锭刀
    public static final Item GUDINGDAO = register("gudingdao", new GudingdaoItem(new Item.Settings()));
    public static final Item GUDING_WEAPON = register("guding_dao", new Equipment.GudingWeapon(new Item.Settings().maxCount(1)));
    //未锻造的古锭刀
    public static final Item INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new Item.Settings().maxCount(1)));
    //古锭
    public static final Item GUDING_ITEM = register("guding", new Item(new Item.Settings()));
    //方天画戟
    public static final Item FANGTIAN = register("fangtian", new Equipment.FangtianWeapon(new Item.Settings().maxCount(1)));
    //寒冰剑
    public static final Item HANBING = register("hanbing", new Equipment.HanbingWeapon(new Item.Settings().maxCount(1)));
    //青釭剑
    public static final Item QINGGANG = register("qinggang", new Equipment.QinggangWeapon(new Item.Settings().maxCount(1)));
    //青龙偃月刀
    public static final Item QINGLONG = register("qinglong", new Equipment.QinglongWeapon(new Item.Settings().maxCount(1)));
    //八卦阵
    public static final Item BAGUA = register("bagua", new Equipment.BaguaArmor(new Item.Settings().maxCount(1)));
    //白银狮子
    public static final Item BAIYIN = register("baiyin", new Equipment.BaiyinArmor(new Item.Settings().maxCount(1)));
    //寿衣
    public static final Item RATTAN_ARMOR = register("rattan_armor", new Equipment.RattanArmor(new Item.Settings().maxCount(1)));
    //-1马
    public static final Item CHITU = register("chitu", new Equipment(new Item.Settings().maxCount(1)));
    //+1马
    public static final Item DILU = register("dilu", new Equipment(new Item.Settings().maxCount(1)));

    //杀
    public static final Item SHA = register("sha", new CardItem.Sha(new Item.Settings()));
    public static final Item FIRE_SHA = register("fire_sha", new CardItem.Sha(new Item.Settings()));
    public static final Item THUNDER_SHA = register("thunder_sha", new CardItem.Sha(new Item.Settings()));
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
    public static final Item ARROW_RAIN = register("arrow_rain", new ArrowRainItem(new Item.Settings().maxDamage(50).rarity(Rarity.UNCOMMON)));
    public static final Item WANJIAN = register("wanjian", new WanjianItem(new Item.Settings()));
    //无懈可击
    public static final Item WUXIE = register("wuxie", new CardItem.Wuxie(new Item.Settings()));
    //无中生有
    public static final Item WUZHONG = register("wuzhong", new GainCardItem(new Item.Settings()));
    //礼盒
    public static final Item GIFTBOX = register("gift_box", new GiftBoxItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    //BB机
    public static final Item BBJI = register("bbji", new BBjiItem(new Item.Settings().maxDamage(250)));
    //让我康康
    public static final Item LET_ME_CC = register("let_me_cc", new LetMeCCItem(new Item.Settings().maxCount(1)));

    public static final Item SUNSHINE_SMILE = register("sunshine_smile", new SunshineSmile(new Item.Settings().maxDamage(999).rarity(Rarity.UNCOMMON)));

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("dabaosword", "item_group"), DABAOSWORD_GROUP);
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
    }

    private static StatusEffect register(String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", id), entry);
    }//状态效果注册
    //兵粮寸断效果
    public static final StatusEffect BINGLIANG = register("bingliang",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,"22653B89-116E-49DC-9B6B-9971489B5BE5",-4, EntityAttributeModifier.Operation.ADDITION));
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
    //铁骑效果
    public static final StatusEffect TIEJI = register("tieji", new CommonEffect(StatusEffectCategory.HARMFUL, 0x07050F));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }

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
                entries.add(SkillCards.ZHIHENG);
                entries.add(SkillCards.ZHIJIAN);
                //群
                entries.add(SkillCards.LEIJI);
                entries.add(SkillCards.LUANJI);
                entries.add(SkillCards.TAOLUAN);
                entries.add(SkillCards.MASHU);
                entries.add(SkillCards.FEIYING);

                entries.add(GIFTBOX);
                entries.add(BBJI);
                entries.add(LET_ME_CC);
                entries.add(SUNSHINE_SMILE);
            }).build();

    public static final ScreenHandlerType<SimpleMenuHandler> SIMPLE_MENU_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "simple_menu", new ExtendedScreenHandlerType<>((syncId, inv, buf) -> new SimpleMenuHandler(syncId, buf)));

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>(PlayerInvScreenHandler::new));

    public static final ScreenHandlerType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "full_inv", new ExtendedScreenHandlerType<>(FullInvScreenHandler::new));
}
