package com.amotassic.dabaosword.items;

import com.amotassic.dabaosword.effect.*;
import com.amotassic.dabaosword.enchantment.LightningAspectEnchantment;
import com.amotassic.dabaosword.enchantment.PojunEnchantment;
import com.amotassic.dabaosword.enchantment.RageNatureEnchantment;
import com.amotassic.dabaosword.items.card.*;
import com.amotassic.dabaosword.items.skillcard.SkillCards;
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
    public static final Item GUDING_ITEM = register("guding", new Item(new FabricItemSettings().maxCount(64)));
    //雷电附加附魔
	public static Enchantment LIGHTNINGASPECT = new LightningAspectEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //破军附魔
    public static Enchantment POJUN = new PojunEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //狂骨附魔
    public static Enchantment  KUANGGU = new RageNatureEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //藤条
    public static final ArmorMaterial RATTAN_MATERIAL = new RattanArmorMaterial();
    public static final Item RATTAN = register("rattan", new Item(new FabricItemSettings().maxCount(64)));
    //寿衣
    public static final Item RATTAN_CHESTPLATE = register("rattan_chestplate", new RattanArmor(RATTAN_MATERIAL,ArmorItem.Type.CHESTPLATE,new Item.Settings()));
    //藤甲
    public static final Item RATTAN_LEGGINGS = register("rattan_leggings", new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    //桃
    public static final Item PEACH = register("peach", new PeachItem(new FabricItemSettings().maxCount(64)));
    //酒  移除public static final Potion COOKING_WINE = new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1), new StatusEffectInstance(StatusEffects.NAUSEA, 300));
    //卡牌形式的酒，为了形式统一而添加
    public static final Item JIU = register("jiu", new JiuItem(new FabricItemSettings().maxCount(16)));
    //万箭齐发
    public static final Item ARROW_RAIN = register("arrow_rain", new ArrowRainItem(new FabricItemSettings().maxDamage(50)));
    //乐不思蜀以及状态效果
    public static final Item TOO_HAPPY_ITEM = register("too_happy", new TooHappyItem(new FabricItemSettings().maxCount(16)));
    public static final StatusEffect TOO_HAPPY = new TooHappyStatusEffect(StatusEffectCategory.HARMFUL, 0xF73C0A);
    //兵粮寸断以及状态效果
    public static final Item BINGLIANG_ITEM = register("bingliang",new BingliangItem(new FabricItemSettings().maxCount(16)));
    public static final StatusEffect BINGLIANG = new BingliangEffect(StatusEffectCategory.HARMFUL, 0x46F732);
    //顺手牵羊
    public static final Item STEAL = register("steal", new StealItem(new FabricItemSettings().maxCount(16)));
    //过河拆桥
    public static final Item DISCARD = register("discard", new DiscardItem(new FabricItemSettings().maxCount(16)));
    //冷却状态效果
    public static final StatusEffect COOLDOWN = new CooldownEffect(StatusEffectCategory.NEUTRAL, 0x000000);
    public static final StatusEffect COOLDOWN2 = new Cooldown2Effect(StatusEffectCategory.NEUTRAL, 0x000000);
    public static final StatusEffect INVULNERABLE = new InvulnerableEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF);
    public static final StatusEffect JUEDOUING = new InvulnerableEffect(StatusEffectCategory.NEUTRAL,0x000000);
    //摸牌
    public static final Item GAIN_CARD = register("gain_card",new GainCardItem(new FabricItemSettings().maxCount(64)));
    //牌堆
    public static final Item CARD_PILE = register("card_pile",new GainCardItem(new FabricItemSettings().maxCount(1)));
    //无中生有
    public static final Item WUZHONG = register("wuzhong", new GainCardItem(new FabricItemSettings().maxCount(64)));
    //闪
    public static final Item SHAN = register("shan", new ShanItem(new FabricItemSettings().maxCount(64)));
    //火攻
    public static final Item FIRE_ATTACK = register("huogong", new FireAttackItem(new FabricItemSettings().maxCount(16)));
    //铁锁连环
    public static final Item TIESUO = register("tiesuo",new TiesuoItem(new FabricItemSettings().maxCount(16)));
    //决斗
    public static final Item JUEDOU = register("juedou",new JuedouItem(new FabricItemSettings().maxCount(16)));
    //无懈可击
    public static final Item WUXIE = register("wuxie", new CardItem(new FabricItemSettings().maxCount(16)));
    //借刀杀人
    public static final Item JIEDAO = register("jiedao", new JiedaoItem(new FabricItemSettings().maxCount(16)));
    public static final Item NANMAN = register("nanman", new NanmanItem(new FabricItemSettings().maxCount(16)));
    //火烧连营
    public static final Item HUOSHAOLIANYING = register("huoshaolianying", new HoushaolianyingItem(new FabricItemSettings().maxCount(16)));

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("dabaosword", "item_group"), DABAOSWORD_GROUP);

        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "lightningaspect"),LIGHTNINGASPECT);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "pojun"),POJUN);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "kuanggu"),KUANGGU);
        //Registry.register(Registries.POTION, new Identifier("dabaosword", "cooking_wine"), COOKING_WINE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "too_happy"), TOO_HAPPY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "bingliang"), BINGLIANG);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "cooldown"), COOLDOWN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "cooldown2"), COOLDOWN2);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "invulnerable"), INVULNERABLE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "juedou"), JUEDOUING);
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
                entries.add(INCOMPLETE_GUDINGDAO);
                entries.add(RATTAN_CHESTPLATE);
                entries.add(RATTAN_LEGGINGS);
                entries.add(ARROW_RAIN);
                entries.add(GUDING_ITEM);
                entries.add(RATTAN);
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
                entries.add(TIESUO);
                entries.add(WUXIE);
                entries.add(WUZHONG);
                entries.add(CARD_PILE);
                entries.add(HUOSHAOLIANYING);

                entries.add(SkillCards.YIJI);
            }).build();
}
