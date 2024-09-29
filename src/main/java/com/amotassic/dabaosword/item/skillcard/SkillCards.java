package com.amotassic.dabaosword.item.skillcard;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillCards {
    //魏
    public static final Item DUANLIANG = register("duanliang", new SkillItem.Duanliang(new Item.Settings().maxCount(1)));
    public static final Item FANGZHU = register("fangzhu", new SkillItem.Fangzhu(new Item.Settings().maxCount(1)));
    public static final Item XINGSHANG = register("xingshang", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item GANGLIE = register("ganglie", new SkillItem.Ganglie(new Item.Settings().maxCount(1)));
    public static final Item GONGAO = register("gongao", new SkillItem.Gongao(new Item.Settings().maxCount(1)));
    public static final Item JUEQING = register("jueqing", new SkillItem.Jueqing(new Item.Settings().maxCount(1)));
    public static final Item LUOSHEN = register("luoshen", new SkillItem.Luoshen(new Item.Settings().maxCount(1)));
    public static final Item QINGGUO = register("qingguo", new SkillItem.Qingguo(new Item.Settings().maxCount(1)));
    public static final Item LUOYI = register("luoyi", new SkillItem.Luoyi(new Item.Settings().maxCount(1)));
    public static final Item QICE = register("qice", new SkillItem.Qice(new Item.Settings().maxCount(1)));
    public static final Item QUANJI = register("quanji", new SkillItem.Quanji(new Item.Settings().maxCount(1)));
    public static final Item SHANZHUAN = register("shanzhuan", new SkillItem.Shanzhuan(new Item.Settings().maxCount(1)));
    public static final Item SHENSU = register("shensu", new SkillItem.Shensu(new Item.Settings().maxCount(1)));
    public static final Item YIJI = register("yiji",new SkillItem.Yiji(new Item.Settings().maxCount(1)));
    //蜀
    public static final Item BENXI = register("benxi", new SkillItem.Benxi(new Item.Settings().maxCount(1)));
    public static final Item HUOJI = register("huoji", new SkillItem.Huoji(new Item.Settings().maxCount(1)));
    public static final Item KANPO = register("kanpo", new SkillItem.Kanpo(new Item.Settings().maxCount(1)));
    public static final Item JIZHI = register("jizhi", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item KUANGGU = register("kuanggu", new SkillItem.Kuanggu(new Item.Settings().maxCount(1)));
    public static final Item LIEGONG = register("liegong", new SkillItem.Liegong(new Item.Settings().maxCount(1)));
    public static final Item LONGDAN = register("longdan", new SkillItem.Longdan(new Item.Settings().maxCount(1)));
    public static final Item RENDE = register("rende", new SkillItem.Rende(new Item.Settings().maxCount(1)));
    public static final Item TIEJI = register("tieji", new SkillItem.Tieji(new Item.Settings().maxCount(1)));
    //吴
    public static final Item BUQU = register("buqu", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item GONGXIN = register("gongxin", new SkillItem.Gongxin(new Item.Settings().maxCount(1)));
    public static final Item GUOSE = register("guose", new SkillItem.Guose(new Item.Settings().maxCount(1)));
    public static final Item LIANYING = register("lianying", new SkillItem.Lianying(new Item.Settings().maxCount(1)));
    public static final Item LIULI = register("liuli", new SkillItem.Liuli(new Item.Settings().maxCount(1)));
    public static final Item KUROU = register("kurou", new SkillItem.Kurou(new Item.Settings().maxCount(1)));
    public static final Item POJUN = register("pojun", new SkillItem.Pojun(new Item.Settings().maxCount(1)));
    public static final Item QIXI = register("qixi", new SkillItem.Qixi(new Item.Settings().maxCount(1)));
    public static final Item XIAOJI = register("xiaoji", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item ZHIHENG = register("zhiheng", new SkillItem.Zhiheng(new Item.Settings().maxCount(1)));
    public static final Item ZHIJIAN = register("zhijian", new SkillItem.Zhijian(new Item.Settings().maxCount(1)));
    //群
    public static final Item LEIJI = register("leiji", new SkillItem(new Item.Settings().maxCount(1)));
    public static final Item LUANJI = register("luanji", new SkillItem.Luanji(new Item.Settings().maxCount(1)));
    public static final Item TAOLUAN = register("taoluan", new SkillItem.Taoluan(new Item.Settings().maxCount(1)));
    public static final Item MASHU = register("mashu", new SkillItem(new Item.Settings().maxCount(1)));

    public static final Item FEIYING = register("feiying", new SkillItem(new Item.Settings().maxCount(1)));

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }

    public static void register() {}
}
