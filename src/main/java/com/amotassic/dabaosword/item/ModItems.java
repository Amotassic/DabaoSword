package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.api.event.*;
import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.entity.ModEntity;
import com.amotassic.dabaosword.event.*;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.equipment.*;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.amotassic.dabaosword.ui.SimpleMenuHandler;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ModItems {
    public static final List<Item> CARDS = new ArrayList<>();
    //杀
    public static final Item SHA = registerCard("sha", new Sha());
    public static final Item FIRE_SHA = registerCard("fire_sha", new Sha.Fire());
    public static final Item THUNDER_SHA = registerCard("thunder_sha", new Sha.Thunder());
    //闪
    public static final Item SHAN = registerCard("shan", new ShanItem());
    //桃
    public static final Item PEACH = registerCard("peach", new PeachItem());
    //酒
    public static final Item JIU = registerCard("jiu", new JiuItem());

    //兵粮寸断
    public static final Item BINGLIANG_ITEM = registerCard("bingliang",new BingliangItem());
    //乐不思蜀
    public static final Item TOO_HAPPY_ITEM = registerCard("too_happy", new TooHappyItem());
    //闪电
    public static final Item SHANDIAN_ITEM = registerCard("shandian", new ShandianItem());
    //过河拆桥
    public static final Item DISCARD = registerCard("discard", new DiscardItem());
    //火攻
    public static final Item FIRE_ATTACK = registerCard("huogong", new FireAttackItem());
    //借刀杀人
    public static final Item JIEDAO = registerCard("jiedao", new JiedaoItem());
    //决斗
    public static final Item JUEDOU = registerCard("juedou",new JuedouItem());
    //南蛮入侵
    public static final Item NANMAN = registerCard("nanman", new NanmanItem());
    //顺手牵羊
    public static final Item STEAL = registerCard("steal", new StealItem());
    //桃园结义
    public static final Item TAOYUAN = registerCard("taoyuan", new TaoyuanItem());
    //铁锁连环
    public static final Item TIESUO = registerCard("tiesuo",new TiesuoItem());
    //万箭齐发
    public static final Item WANJIAN = registerCard("wanjian", new WanjianItem());
    //五谷丰登
    public static final Item WUGU = registerCard("wugu", new WuguItem());
    //无懈可击
    public static final Item WUXIE = registerCard("wuxie", new CardItem());
    //无中生有
    public static final Item WUZHONG = registerCard("wuzhong", new CardItem.Wuzhong());

    //雌雄双股剑
    public static final Item CIXIONG = registerCard("cixiong", new Equipment.CixiongWeapon());
    //方天画戟
    public static final Item FANGTIAN = registerCard("fangtian", new Equipment.FangtianWeapon());
    //贯石斧
    public static final Item GUANSHI = registerCard("guanshi", new Equipment.GuanshiWeapon());
    // 古锭刀
    public static final Item GUDING_WEAPON = registerCard("guding_dao", new Equipment.GudingWeapon());
    //寒冰剑
    public static final Item HANBING = registerCard("hanbing", new Equipment.HanbingWeapon());
    //麒麟弓
    public static final Item QILIN = registerCard("qilin", new Equipment.QilinWeapon());
    //青釭剑
    public static final Item QINGGANG = registerCard("qinggang", new Equipment.QinggangWeapon());
    //青龙偃月刀
    public static final Item QINGLONG = registerCard("qinglong", new Equipment.QinglongWeapon());
    //丈八蛇矛
    public static final Item ZHANGBA = registerCard("zhangba", new Equipment.ZhangbaWeapon());
    //诸葛连弩
    public static final Item LIANNU = registerCard("liannu", new Equipment.LiannuWeapon());
    //朱雀羽扇
    public static final Item ZHUQUE = registerCard("zhuque", new Equipment.ZhuqueWeapon());
    //八卦阵
    public static final Item BAGUA = registerCard("bagua", new Equipment.BaguaArmor());
    //白银狮子
    public static final Item BAIYIN = registerCard("baiyin", new Equipment.BaiyinArmor());
    //仁王盾
    public static final Item RENWANG = registerCard("renwang", new Equipment.RenwangArmor());
    //寿衣
    public static final Item RATTAN_ARMOR = registerCard("rattan_armor", new Equipment.RattanArmor());
    //-1马
    public static final Item CHITU = registerCard("chitu", new Equipment.AttackHorse());
    //+1马
    public static final Item DILU = registerCard("dilu", new Equipment.DefendHorse());

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
    public static final Item XUYOU_SPAWN_EGG = register("xuyou_spawn_egg", new SpawnEggItem(ModEntity.XUYOU, 0x52BDF7, 0x8D8B96, new Item.Settings()));
    public static final Item GUDING_ITEM = register("guding", new Item(new Item.Settings()));
    public static final Item INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new Item.Settings().maxCount(1)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }
    /**将注册的卡牌添加到卡牌列表中，便于自动将物品添加到物品组*/
    public static Item registerCard(String name, Item item) {
        Item card = register(name, item);
        CARDS.add(card);
        return card;
    }

    public static Map<String, Item> CUSTOM_SKILLS = new HashMap<>();
    public static final File CUSTOM_SKILLS_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "dabaosword/custom_skill");

    /*@SuppressWarnings({"ResultOfMethodCallIgnored", "CallToPrintStackTrace", "resource"})
    private static void registerCustomSkills() {
        if (!CUSTOM_SKILLS_DIR.exists()) CUSTOM_SKILLS_DIR.mkdirs();

        Path from = FabricLoader.getInstance().getModContainer("dabaosword").orElseThrow().getRootPaths().get(0).resolve("data/dabaosword/custom_skill");
        Path to = FabricLoader.getInstance().getConfigDir().resolve("dabaosword/custom_skill");
        try {
            Files.walk(from).forEach(sourcePath -> {
                Path targetPath = to.resolve(from.relativize(sourcePath));

                if (!Files.exists(targetPath)) {
                    try {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {e.printStackTrace();}
                }
            });
        } catch (IOException e) {e.printStackTrace();}

        final File[] files = CUSTOM_SKILLS_DIR.listFiles();
        if (files != null) {
            for (File file : files) {
                final String name = file.getName().substring(0, file.getName().length() - (".json".length()));
                final Item item = register(name, new CustomSkillItem(name));
                CUSTOM_SKILLS.put(name, item);
            }
        }
    }*/

    public static final RegistryKey<ItemGroup> ZZRS = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("dabaosword", "item_group"));

    private static void addToGroup(FabricItemGroupEntries entries) {
        //添加所有卡牌
        for (Item item : CARDS) entries.add(item);
        entries.add(GAIN_CARD);
        entries.add(CARD_PILE);
        //添加所有技能
        for (Item item : SkillCards.SKILLS) entries.add(item);

        for (Item item : CUSTOM_SKILLS.values()) entries.add(item);

        entries.add(GIFTBOX);
        entries.add(BBJI);
        entries.add(LET_ME_CC);
        entries.add(SUNSHINE_SMILE);
        entries.add(XUYOU_SPAWN_EGG);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ZZRS,
                FabricItemGroup.builder().icon(() -> new ItemStack(SUNSHINE_SMILE))
                        .displayName(Text.translatable("itemGroup.dabaosword.item_group")).build());
        ItemGroupEvents.modifyEntriesEvent(ZZRS).register(ModItems::addToGroup);

        ServerWorldEvents.LOAD.register(new PVPGameEvents());
        ServerTickEvents.START_WORLD_TICK.register(new PVPGameEvents());
        PVPGameTickCallback.EVENT.register(new PVPGameEvents());
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
        EntityHurtCallback.EVENT.register(new EntityHurtHandler());
        PlayerDeathCallback.EVENT.register(new PlayerEvents());
        PlayerRespawnCallback.EVENT.register(new PlayerEvents());
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
            new TooHappyEffect().addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,"7107DE5E-7CE8-4030-940E-514C1F160890",-10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    //触及距离增加
    public static final StatusEffect REACH = register("reach", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(ReachEntityAttributes.REACH,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION));
    //近战防御范围增加
    public static final StatusEffect DEFEND = register("defend", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final StatusEffect DEFENDED = register("defended",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"6656ba40-7a9c-a584-3c63-1e1e0e655446",-1.0, EntityAttributeModifier.Operation.ADDITION));
    //冷却状态效果
    public static final StatusEffect COOLDOWN = register("cooldown", new CooldownEffect());
    public static final StatusEffect COOLDOWN2 = register("cooldown2", new Cooldown2Effect());
    //无敌效果
    public static final StatusEffect INVULNERABLE = register("invulnerable", new CommonEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF));
    //下落攻击效果
    public static final StatusEffect FALLING_ATTACK = register("falling_attack", new FallingEffect());
    //翻面效果
    public static final StatusEffect TURNOVER = register("turn_over", new TurnOverEffect());
    //铁骑效果
    public static final StatusEffect TIEJI = register("tieji", new CommonEffect(StatusEffectCategory.HARMFUL, 0x07050F));
    //闪电效果
    public static final StatusEffect SHANDIAN = register("shandian", new ShandianEffect());

    public static final ScreenHandlerType<SimpleMenuHandler> SIMPLE_MENU_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "simple_menu", new ExtendedScreenHandlerType<>(SimpleMenuHandler::new));

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>(PlayerInvScreenHandler::new));

    public static final ScreenHandlerType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "full_inv", new ExtendedScreenHandlerType<>(FullInvScreenHandler::new));

    public static final ScreenHandlerType<PileScreenHandler> PILE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "card_pile", new ExtendedScreenHandlerType<>(PileScreenHandler::new));
}
