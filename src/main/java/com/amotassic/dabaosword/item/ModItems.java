package com.amotassic.dabaosword.item;

import com.amotassic.dabaosword.enchantment.LightningAspectEnchantment;
import com.amotassic.dabaosword.enchantment.PojunEnchantment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // 古锭刀
    public static final GudingdaoItem GUDINGDAO_ITEM = new GudingdaoItem(new FabricItemSettings());
    //未锻造的古锭刀
    public static final Item INCOMPLETE_GUDINGDAO_ITEM = new Item(new FabricItemSettings().maxCount(1));
    //古锭
    public static final Item GUDING_ITEM = new Item(new FabricItemSettings().maxCount(64));
    //雷电附加附魔
	public static Enchantment LIGHTNINGASPECT = new LightningAspectEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //破军附魔
    public static Enchantment POJUN = new PojunEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    //藤条
    public static final ArmorMaterial RATTAN_MATERIAL = new RattanArmorMaterial();
    public static final Item RATTAN = new Item(new FabricItemSettings().maxCount(64));
    //寿衣
    public static final Item RATTAN_CHESTPLATE = new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    //藤甲
    public static final Item RATTAN_LEGGINGS = new RattanArmor(RATTAN_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    //桃
    public static final Item PEACH = new PeachItem(new FabricItemSettings().maxCount(64));

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "gudingdao"), GUDINGDAO_ITEM);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "guding"), GUDING_ITEM);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "incomplete_gdd"), INCOMPLETE_GUDINGDAO_ITEM);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "lightningaspect"),LIGHTNINGASPECT);
        Registry.register(Registries.ENCHANTMENT, new Identifier("dabaosword", "pojun"),POJUN);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan"), RATTAN);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan_chestplate"), RATTAN_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "rattan_leggings"), RATTAN_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier("dabaosword", "peach"), PEACH);
    }
}
