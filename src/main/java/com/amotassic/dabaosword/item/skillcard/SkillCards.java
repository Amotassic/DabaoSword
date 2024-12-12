package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.skillcard.skills.Qun;
import com.amotassic.dabaosword.item.skillcard.skills.Shu;
import com.amotassic.dabaosword.item.skillcard.skills.Wei;
import com.amotassic.dabaosword.item.skillcard.skills.Wu;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillCards {
    //魏
    public static final Item DUANLIANG = register("duanliang", new Wei.Duanliang());
    public static final Item FANGZHU = register("fangzhu", new Wei.Fangzhu());
    public static final Item XINGSHANG = register("xingshang", new Wei.Xingshang());
    public static final Item GANGLIE = register("ganglie", new Wei.Ganglie());
    public static final Item GONGAO = register("gongao", new Wei.Gongao());
    public static final Item JIANXIONG = register("jianxiong", new Wei.Jianxiong());
    public static final Item JUEQING = register("jueqing", new Wei.Jueqing());
    public static final Item LUOSHEN = register("luoshen", new Wei.Luoshen());
    public static final Item QINGGUO = register("qingguo", new Wei.Qingguo());
    public static final Item LUOYI = register("luoyi", new Wei.Luoyi());
    public static final Item QICE = register("qice", new Wei.Qice());
    public static final Item QUANJI = register("quanji", new Wei.Quanji());
    public static final Item SHANZHUAN = register("shanzhuan", new Wei.Shanzhuan());
    public static final Item SHENSU = register("shensu", new Wei.Shensu());
    public static final Item YIJI = register("yiji",new Wei.Yiji());
    //蜀
    public static final Item BENXI = register("benxi", new Shu.Benxi());
    public static final Item HUOJI = register("huoji", new Shu.Huoji());
    public static final Item KANPO = register("kanpo", new Shu.Kanpo());
    public static final Item JIZHI = register("jizhi", new Shu.Jizhi());
    public static final Item KUANGGU = register("kuanggu", new Shu.Kuanggu());
    public static final Item LIEGONG = register("liegong", new Shu.Liegong());
    public static final Item LONGDAN = register("longdan", new Shu.Longdan());
    public static final Item RENDE = register("rende", new Shu.Rende());
    public static final Item TIEJI = register("tieji", new Shu.Tieji());
    public static final Item WUSHENG = register("wusheng", new Shu.Wusheng());
    //吴
    public static final Item BUQU = register("buqu", new Wu.Buqu());
    public static final Item FENYIN = register("fenyin", new Wu.Fenyin());
    public static final Item GONGXIN = register("gongxin", new Wu.Gongxin());
    public static final Item GUOSE = register("guose", new Wu.Guose());
    public static final Item LIANYING = register("lianying", new Wu.Lianying());
    public static final Item LIULI = register("liuli", new Wu.Liuli());
    public static final Item KUROU = register("kurou", new Wu.Kurou());
    public static final Item POJUN = register("pojun", new Wu.Pojun());
    public static final Item QIXI = register("qixi", new Wu.Qixi());
    public static final Item XIAOJI = register("xiaoji", new Wu.Xiaoji());
    public static final Item YINGZI = register("yingzi", new Wu.Yingzi());
    public static final Item ZHIHENG = register("zhiheng", new Wu.Zhiheng());
    public static final Item ZHIJIAN = register("zhijian", new Wu.Zhijian());
    //群
    public static final Item JIJIU = register("jijiu", new Qun.Jijiu());
    public static final Item JIUCHI = register("jiuchi", new Qun.Jiuchi());
    public static final Item JIZHAN = register("jizhan", new Qun.Jizhan());
    public static final Item LEIJI = register("leiji", new Qun.Leiji());
    public static final Item LUANJI = register("luanji", new Qun.Luanji());
    public static final Item TAOLUAN = register("taoluan", new Qun.Taoluan());
    public static final Item WEIMU = register("weimu", new Qun.Weimu());
    public static final Item MASHU = register("mashu", new Qun.Mashu());

    public static final Item FEIYING = register("feiying", new Qun.Feiying());

    private static Item register(String name,Item item){
        return Registry.register(Registries.ITEM, Identifier.of("dabaosword", name), item);
    }

    public static void register() {}

}
