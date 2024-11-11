package com.amotassic.dabaosword.item.skillcard;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillCards {
    //魏
    public static final Item DUANLIANG = register("duanliang", new SkillItem.Duanliang());
    public static final Item FANGZHU = register("fangzhu", new SkillItem.Fangzhu());
    public static final Item XINGSHANG = register("xingshang", new SkillItem());
    public static final Item GANGLIE = register("ganglie", new SkillItem.Ganglie());
    public static final Item GONGAO = register("gongao", new SkillItem.Gongao());
    public static final Item JIANXIONG = register("jianxiong", new SkillItem.Jianxiong());
    public static final Item JUEQING = register("jueqing", new SkillItem.Jueqing());
    public static final Item LUOSHEN = register("luoshen", new SkillItem.Luoshen());
    public static final Item QINGGUO = register("qingguo", new SkillItem.Qingguo());
    public static final Item LUOYI = register("luoyi", new SkillItem.Luoyi());
    public static final Item QICE = register("qice", new SkillItem.Qice());
    public static final Item QUANJI = register("quanji", new SkillItem.Quanji());
    public static final Item SHANZHUAN = register("shanzhuan", new SkillItem.Shanzhuan());
    public static final Item SHENSU = register("shensu", new SkillItem.Shensu());
    public static final Item YIJI = register("yiji",new SkillItem.Yiji());
    //蜀
    public static final Item BENXI = register("benxi", new SkillItem.Benxi());
    public static final Item HUOJI = register("huoji", new SkillItem.Huoji());
    public static final Item KANPO = register("kanpo", new SkillItem.Kanpo());
    public static final Item JIZHI = register("jizhi", new SkillItem());
    public static final Item KUANGGU = register("kuanggu", new SkillItem.Kuanggu());
    public static final Item LIEGONG = register("liegong", new SkillItem.Liegong());
    public static final Item LONGDAN = register("longdan", new SkillItem.Longdan());
    public static final Item RENDE = register("rende", new SkillItem.Rende());
    public static final Item TIEJI = register("tieji", new SkillItem.Tieji());
    public static final Item WUSHENG = register("wusheng", new SkillItem.Wusheng());
    //吴
    public static final Item BUQU = register("buqu", new SkillItem.Buqu());
    public static final Item GONGXIN = register("gongxin", new SkillItem.Gongxin());
    public static final Item GUOSE = register("guose", new SkillItem.Guose());
    public static final Item LIANYING = register("lianying", new SkillItem.Lianying());
    public static final Item LIULI = register("liuli", new SkillItem.Liuli());
    public static final Item KUROU = register("kurou", new SkillItem.Kurou());
    public static final Item POJUN = register("pojun", new SkillItem.Pojun());
    public static final Item QIXI = register("qixi", new SkillItem.Qixi());
    public static final Item XIAOJI = register("xiaoji", new SkillItem());
    public static final Item YINGZI = register("yingzi", new SkillItem.Yingzi());
    public static final Item ZHIHENG = register("zhiheng", new SkillItem.Zhiheng());
    public static final Item ZHIJIAN = register("zhijian", new SkillItem.Zhijian());
    //群
    public static final Item JIZHAN = register("jizhan", new SkillItem.Jizhan());
    public static final Item LEIJI = register("leiji", new SkillItem());
    public static final Item LUANJI = register("luanji", new SkillItem.Luanji());
    public static final Item TAOLUAN = register("taoluan", new SkillItem.Taoluan());
    public static final Item WEIMU = register("weimu", new SkillItem.Weimu());
    public static final Item MASHU = register("mashu", new SkillItem());

    public static final Item FEIYING = register("feiying", new SkillItem());

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
    }

    public static void register() {}
}
