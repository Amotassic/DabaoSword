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
import com.amotassic.dabaosword.ui.FullInvScreenHandler;
import com.amotassic.dabaosword.ui.PileScreenHandler;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
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
    public static final List<CardItem> CARDS = new ArrayList<>();
    //杀
    public static final CardItem
    SHA = registerCard("sha", new Sha()),
    FIRE_SHA = registerCard("fire_sha", new Sha.Fire()),
    THUNDER_SHA = registerCard("thunder_sha", new Sha.Thunder()),
    //闪
    SHAN = registerCard("shan", new ShanItem()),
    //桃
    PEACH = registerCard("peach", new PeachItem()),
    //酒
    JIU = registerCard("jiu", new JiuItem()),

    //兵粮寸断
    BINGLIANG_ITEM = registerCard("bingliang",new BingliangItem()),
    //乐不思蜀
    TOO_HAPPY_ITEM = registerCard("too_happy", new TooHappyItem()),
    //闪电
    SHANDIAN_ITEM = registerCard("shandian", new ShandianItem()),
    //过河拆桥
    DISCARD = registerCard("discard", new DiscardItem()),
    //火攻
    FIRE_ATTACK = registerCard("huogong", new FireAttackItem()),
    //借刀杀人
    JIEDAO = registerCard("jiedao", new JiedaoItem()),
    //决斗
    JUEDOU = registerCard("juedou",new JuedouItem()),
    //南蛮入侵
    NANMAN = registerCard("nanman", new NanmanItem()),
    //顺手牵羊
    STEAL = registerCard("steal", new StealItem()),
    //桃园结义
    TAOYUAN = registerCard("taoyuan", new TaoyuanItem()),
    //铁锁连环
    TIESUO = registerCard("tiesuo",new TiesuoItem()),
    //万箭齐发
    WANJIAN = registerCard("wanjian", new WanjianItem()),
    //五谷丰登
    WUGU = registerCard("wugu", new WuguItem()),
    //无懈可击
    WUXIE = registerCard("wuxie", new CardItem.Armoury()),
    //无中生有
    WUZHONG = registerCard("wuzhong", new WuzhongItem()),

    //雌雄双股剑
    CIXIONG = registerCard("cixiong", new Weapon.Cixiong()),
    //方天画戟
    FANGTIAN = registerCard("fangtian", new Weapon.Fangtian()),
    //贯石斧
    GUANSHI = registerCard("guanshi", new Weapon.Guanshi()),
    // 古锭刀
    GUDING_WEAPON = registerCard("guding_dao", new Weapon.Guding()),
    //寒冰剑
    HANBING = registerCard("hanbing", new Weapon.Hanbing()),
    //麒麟弓
    QILIN = registerCard("qilin", new Weapon.Qilin()),
    //青釭剑
    QINGGANG = registerCard("qinggang", new Weapon.Qinggang()),
    //青龙偃月刀
    QINGLONG = registerCard("qinglong", new Weapon.Qinglong()),
    //丈八蛇矛
    ZHANGBA = registerCard("zhangba", new Weapon.Zhangba()),
    //诸葛连弩
    LIANNU = registerCard("liannu", new Weapon.Liannu()),
    //朱雀羽扇
    ZHUQUE = registerCard("zhuque", new Weapon.Zhuque()),
    //八卦阵
    BAGUA = registerCard("bagua", new Armor.Bagua()),
    //白银狮子
    BAIYIN = registerCard("baiyin", new Armor.Baiyin()),
    //仁王盾
    RENWANG = registerCard("renwang", new Armor.Renwang()),
    //寿衣
    RATTAN_ARMOR = registerCard("rattan_armor", new Armor.Rattan()),
    //-1马
    CHITU = registerCard("chitu", new Mount.Attack()),
    //+1马
    DILU = registerCard("dilu", new Mount.Defend());

    public static final Item
    //摸牌
    GAIN_CARD = register("gain_card", new GainCardItem()),
    //牌堆
    CARD_PILE = register("card_pile", new CardPile()),
    //礼盒
    GIFTBOX = register("gift_box", new GiftBoxItem()),
    GUDINGDAO = register("gudingdao", new GudingdaoItem()),
    ARROW_RAIN = register("arrow_rain", new ArrowRainItem()),
    //BB机
    BBJI = register("bbji", new BBjiItem()),
    //让我康康
    LET_ME_CC = register("let_me_cc", new LetMeCCItem()),
    //阳光开朗的笑容
    SUNSHINE_SMILE = register("sunshine_smile", new SunshineSmile()),
    XUYOU_SPAWN_EGG = register("xuyou_spawn_egg", new SpawnEggItem(ModEntity.XUYOU, 0x52BDF7, 0x8D8B96, new Item.Settings())),
    GUDING_ITEM = register("guding", new Item(new Item.Settings())),
    INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new Item.Settings().maxCount(1)));
    public static final CardItem EMPTY_CARD = register("empty_card", new CardItem.Empty());
    public static final SkillItem EMPTY_SKILL = register("empty_skill", new SkillItem());

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }
    /**将注册的卡牌添加到卡牌列表中，便于自动将物品添加到物品组*/
    public static CardItem registerCard(String name, CardItem item) {
        CardItem card = register(name, item);
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

    public static final RegistryKey<ItemGroup> ZZRS = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("dabaosword", "zzrs"));

    private static void addToGroup(ItemGroup.DisplayContext context, ItemGroup.Entries entries) {
        ItemStack smile = new ItemStack(SUNSHINE_SMILE);
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("Unbreakable", true);
        smile.setNbt(nbt);
        smile.addEnchantment(CRIT, 1);
        //添加所有卡牌
        CARDS.forEach(entries::add);
        entries.add(GAIN_CARD);
        entries.add(CARD_PILE);
        //添加所有技能
        SkillCards.SKILLS.forEach(entries::add);

        for (Item item : CUSTOM_SKILLS.values()) entries.add(item);

        entries.add(GIFTBOX);
        entries.add(BBJI);
        entries.add(LET_ME_CC);
        entries.add(smile);
        entries.add(XUYOU_SPAWN_EGG);
    }

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

    private static StatusEffect register(String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", id), entry);
    }//状态效果注册
    //兵粮寸断效果
    public static final StatusEffect BINGLIANG = register("bingliang",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0x46F732).addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,"22653B89-116E-49DC-9B6B-9971489B5BE5",-4, EntityAttributeModifier.Operation.ADDITION)),
    //乐不思蜀效果
    TOO_HAPPY = register("too_happy", new TooHappyEffect().addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,"7107DE5E-7CE8-4030-940E-514C1F160890",-10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
    //触及距离增加
    REACH = register("reach", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)
            .addAttributeModifier(ReachEntityAttributes.REACH,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION)
            .addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"2b3df518-6e44-3554-821b-232333bcef5b",1.0, EntityAttributeModifier.Operation.ADDITION)),
    //近战防御范围增加
    DEFEND = register("defend", new CommonEffect(StatusEffectCategory.BENEFICIAL, 0xFFFFFF)),
    DEFENDED = register("defended",
            new CommonEffect(StatusEffectCategory.HARMFUL, 0xFFFFFF).addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE,"6656ba40-7a9c-a584-3c63-1e1e0e655446",-1.0, EntityAttributeModifier.Operation.ADDITION)),
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

    public static final ScreenHandlerType<PlayerInvScreenHandler> PLAYER_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "player_inv", new ExtendedScreenHandlerType<>(PlayerInvScreenHandler::new));

    public static final ScreenHandlerType<FullInvScreenHandler> FULL_INV_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "full_inv", new ExtendedScreenHandlerType<>(FullInvScreenHandler::new));

    public static final ScreenHandlerType<PileScreenHandler> PILE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, "card_pile", new ExtendedScreenHandlerType<>(PileScreenHandler::new));

    public static final RegistryKey<DamageType> LOSEHP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dabaosword", "losehp"));

    public static final Enchantment CRIT = Registry.register(Registries.ENCHANTMENT, "dabaosword:crit", new CritEnchantment(EquipmentSlot.HEAD));

    public static class CritEnchantment extends Enchantment {
        public CritEnchantment(EquipmentSlot... slots) {super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_HEAD, slots);}
        @Override public boolean isTreasure() {return true;}
    }

}
