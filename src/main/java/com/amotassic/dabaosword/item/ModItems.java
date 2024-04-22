package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.enchantment.LightningAspectEnchantment;
import com.amotassic.dabaosword.item.card.*;
import com.amotassic.dabaosword.item.card.GiftBoxItem;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    // 古锭刀
    public static final Item GUDINGDAO = register("gudingdao", new GudingdaoItem(new FabricItemSettings()));
    //未锻造的古锭刀
    public static final Item INCOMPLETE_GUDINGDAO = register("incomplete_gdd", new Item(new FabricItemSettings().maxCount(1)));
    //古锭
    public static final Item GUDING_ITEM = register("guding", new Item(new FabricItemSettings()));
    //雷电附加附魔
	public static Enchantment LIGHTNINGASPECT = new LightningAspectEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //藤条
    public static final ArmorMaterial RATTAN_MATERIAL = new RattanArmorMaterial();
    public static final Item RATTAN = register("rattan", new Item(new FabricItemSettings()));
    //寿衣
    public static final Item RATTAN_CHESTPLATE = register("rattan_chestplate", new RattanArmor(RATTAN_MATERIAL,ArmorItem.Type.CHESTPLATE,new Item.Settings()));
    //藤甲
    public static final Item RATTAN_LEGGINGS = register("rattan_leggings", new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    //桃
    public static final Item PEACH = register("peach", new PeachItem(new FabricItemSettings()));
    //酒
    public static final Item JIU = register("jiu", new JiuItem(new FabricItemSettings()));
    //万箭齐发
    public static final Item ARROW_RAIN = register("arrow_rain", new ArrowRainItem(new FabricItemSettings().maxDamage(50)));
    public static final Item WANJIAN = register("wanjian", new WanjianItem(new FabricItemSettings()));
    //乐不思蜀以及状态效果
    public static final Item TOO_HAPPY_ITEM = register("too_happy", new TooHappyItem(new FabricItemSettings()));
    public static final StatusEffect TOO_HAPPY = new TooHappyStatusEffect(StatusEffectCategory.HARMFUL, 0xF73C0A);
    //兵粮寸断以及状态效果
    public static final Item BINGLIANG_ITEM = register("bingliang",new BingliangItem(new FabricItemSettings()));
    public static final StatusEffect BINGLIANG = new BingliangEffect(StatusEffectCategory.HARMFUL, 0x46F732);
    //顺手牵羊
    public static final Item STEAL = register("steal", new StealItem(new FabricItemSettings()));
    //过河拆桥
    public static final Item DISCARD = register("discard", new DiscardItem(new FabricItemSettings()));
    //冷却状态效果
    public static final StatusEffect COOLDOWN = new CooldownEffect(StatusEffectCategory.NEUTRAL, 0x000000);
    public static final StatusEffect COOLDOWN2 = new Cooldown2Effect(StatusEffectCategory.NEUTRAL, 0x000000);
    public static final StatusEffect INVULNERABLE = new InvulnerableEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF);
    public static final StatusEffect JUEDOUING = new InvulnerableEffect(StatusEffectCategory.NEUTRAL,0x000000);
    //摸牌
    public static final Item GAIN_CARD = register("gain_card",new GainCardItem(new FabricItemSettings()));
    //牌堆
    public static final Item CARD_PILE = register("card_pile",new GainCardItem(new FabricItemSettings().maxCount(1)));
    //无中生有
    public static final Item WUZHONG = register("wuzhong", new GainCardItem(new FabricItemSettings()));
    //闪
    public static final Item SHAN = register("shan", new ShanItem(new FabricItemSettings()));
    //火攻
    public static final Item FIRE_ATTACK = register("huogong", new FireAttackItem(new FabricItemSettings()));
    //铁锁连环
    public static final Item TIESUO = register("tiesuo",new TiesuoItem(new FabricItemSettings()));
    //决斗
    public static final Item JUEDOU = register("juedou",new JuedouItem(new FabricItemSettings()));
    //无懈可击
    public static final Item WUXIE = register("wuxie", new CardItem(new FabricItemSettings()));
    //借刀杀人
    public static final Item JIEDAO = register("jiedao", new JiedaoItem(new FabricItemSettings()));
    //南蛮入侵
    public static final Item NANMAN = register("nanman", new NanmanItem(new FabricItemSettings()));
    //青釭剑
    public static final Item QINGGANG = register("qinggang", new QinggangjianItem());
    //桃园结义
    public static final Item TAOYUAN = register("taoyuan", new TaoyuanItem(new FabricItemSettings()));
    //触及距离增加
    public static final Item CHITU = register("chitu", new ChituItem(new FabricItemSettings()));
    public static final StatusEffect REACH = new ReachEffect(StatusEffectCategory.BENEFICIAL, 0x000000);
    //近战防御范围增加
    public static final Item DILU = register("dilu", new DiluItem(new FabricItemSettings()));
    public static final StatusEffect DEFENSE = new DefenseEffect(StatusEffectCategory.BENEFICIAL, 0x000000);
    //礼盒
    public static final Item GIFTBOX = register("gift_box", new GiftBoxItem(new FabricItemSettings()));

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("dabaosword", "item_group"), DABAOSWORD_GROUP);

        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "lightningaspect"),LIGHTNINGASPECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "too_happy"), TOO_HAPPY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "bingliang"), BINGLIANG);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "cooldown"), COOLDOWN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "cooldown2"), COOLDOWN2);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "invulnerable"), INVULNERABLE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "juedou"), JUEDOUING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "reach"), REACH);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "defense"), DEFENSE);
    }

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier("dabaosword",name),item);
    }

    public static void registerItems() {}

    //物品组添加
    public static final ItemGroup DABAOSWORD_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GUDINGDAO))
            .displayName(Text.translatable("itemGroup.dabaosword.item_group"))
            .entries((context, entries) -> {
                entries.add(GUDINGDAO);
                entries.add(QINGGANG);
                entries.add(INCOMPLETE_GUDINGDAO);
                entries.add(RATTAN_CHESTPLATE);
                entries.add(RATTAN_LEGGINGS);
                entries.add(ARROW_RAIN);
                entries.add(GUDING_ITEM);
                entries.add(RATTAN);
                entries.add(CHITU);
                entries.add(DILU);
                entries.add(GAIN_CARD);
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
                entries.add(CARD_PILE);

                entries.add(SkillCards.JUEQING);
                entries.add(SkillCards.LUOYI);
                entries.add(SkillCards.QICE);
                entries.add(SkillCards.QUANJI);
                entries.add(SkillCards.YIJI);
                entries.add(SkillCards.HUOJI);
                entries.add(SkillCards.KANPO);
                entries.add(SkillCards.JIZHI);
                entries.add(SkillCards.KUANGGU);
                entries.add(SkillCards.GUOSE);
                entries.add(SkillCards.LIULI);
                entries.add(SkillCards.KUROU);
                entries.add(SkillCards.POJUN);
                entries.add(SkillCards.LEIJI);
                entries.add(SkillCards.LUANJI);
                entries.add(SkillCards.TAOLUAN);
                entries.add(SkillCards.MASHU);
                entries.add(SkillCards.FEIYING);

                entries.add(ModItems.GIFTBOX);
            }).build();
}
