package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.effect.CooldownEffect;
import com.amotassic.dabaosword.effect.InvulnerableEffect;
import com.amotassic.dabaosword.effect.TooHappyStatusEffect;
import com.amotassic.dabaosword.enchantment.LightningAspectEnchantment;
import com.amotassic.dabaosword.enchantment.PojunEnchantment;
import com.amotassic.dabaosword.enchantment.RageNatureEnchantment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    // 古锭刀
    public static final Item GUDINGDAO = new GudingdaoItem(new FabricItemSettings());
    //未锻造的古锭刀
    public static final Item INCOMPLETE_GUDINGDAO = new Item(new FabricItemSettings().maxCount(1));
    //古锭
    public static final Item GUDING_ITEM = new Item(new FabricItemSettings().maxCount(64));
    //雷电附加附魔
	public static Enchantment LIGHTNINGASPECT = new LightningAspectEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //破军附魔
    public static Enchantment POJUN = new PojunEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //狂骨附魔
    public static Enchantment  KUANGGU = new RageNatureEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //藤条
    public static final ArmorMaterial RATTAN_MATERIAL = new RattanArmorMaterial();
    public static final Item RATTAN = new Item(new FabricItemSettings().maxCount(64));
    //寿衣
    public static final Item RATTAN_CHESTPLATE = new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    //藤甲
    public static final Item RATTAN_LEGGINGS = new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    //桃
    public static final Item PEACH = new PeachItem(new FabricItemSettings().maxCount(64));
    //酒
    public static final Potion COOKING_WINE = new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1), new StatusEffectInstance(StatusEffects.NAUSEA, 300));
    //万箭齐发
    public static final Item ARROW_RAIN = new ArrowRainItem(new FabricItemSettings().maxCount(1).maxDamage(50));
    //乐不思蜀以及状态效果
    public static final Item TOO_HAPPY_ITEM = new TooHappyItem(new FabricItemSettings().maxCount(16));
    public static final StatusEffect TOO_HAPPY = new TooHappyStatusEffect(StatusEffectCategory.HARMFUL, 0xF73C0A);
    //顺手牵羊
    public static final Item STEAL = new StealItem(new FabricItemSettings().maxCount(16));
    //过河拆桥
    public static final Item DISCARD = new DiscardItem(new FabricItemSettings().maxCount(16));
    //冷却状态效果
    public static final StatusEffect COOLDOWN = new CooldownEffect(StatusEffectCategory.NEUTRAL, 0x000000);
    public static final StatusEffect INVULNERABLE = new InvulnerableEffect(StatusEffectCategory.BENEFICIAL,0x35F5DF);
    //摸牌
    public static final Item GAIN_CARD = new GainCardItem(new FabricItemSettings().maxCount(64));
    //牌堆
    public static final Item CARD_PILE = new GainCardItem(new FabricItemSettings().maxCount(1));
    //无中生有
    public static final Item WUZHONG = new WuzhongItem(new FabricItemSettings().maxCount(64));
    public static final Item SHAN = new ShanItem(new FabricItemSettings().maxCount(64));

    //注册部分
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("dabaosword", "item_group"), DABAOSWORD_GROUP);

        Registry.register(Registries.ITEM, new Identifier("dabaosword", "gudingdao"), GUDINGDAO);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "guding"), GUDING_ITEM);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "incomplete_gdd"), INCOMPLETE_GUDINGDAO);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "lightningaspect"),LIGHTNINGASPECT);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "pojun"),POJUN);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "kuanggu"),KUANGGU);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan"), RATTAN);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan_chestplate"), RATTAN_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan_leggings"), RATTAN_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "peach"), PEACH);
        Registry.register(Registries.POTION, new Identifier("dabaosword", "cooking_wine"), COOKING_WINE);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "arrow_rain"), ARROW_RAIN);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "too_happy"), TOO_HAPPY_ITEM);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "too_happy"), TOO_HAPPY);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "steal"), STEAL);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "discard"), DISCARD);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "cooldown"), COOLDOWN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dabaosword", "invulnerable"), INVULNERABLE);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "gain_card"), GAIN_CARD);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "card_pile"), CARD_PILE);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "wuzhong"), WUZHONG);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "shan"), SHAN);
    }

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
                entries.add(TOO_HAPPY_ITEM);
                entries.add(STEAL);
                entries.add(DISCARD);
                entries.add(WUZHONG);
                entries.add(CARD_PILE);
            }).build();
}
