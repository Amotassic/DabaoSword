package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.skillcard.skills.Qun;
import com.amotassic.dabaosword.item.skillcard.skills.Shu;
import com.amotassic.dabaosword.item.skillcard.skills.Wei;
import com.amotassic.dabaosword.item.skillcard.skills.Wu;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SkillCards {
    public static final List<Item> SKILLS = new ArrayList<>();
    //魏
    public static final Item
    DUANLIANG = register("duanliang", new Wei.Duanliang()),
    FANGZHU = register("fangzhu", new Wei.Fangzhu()),
    XINGSHANG = register("xingshang", new Wei.Xingshang()),
    GANGLIE = register("ganglie", new Wei.Ganglie()),
    GONGAO = register("gongao", new Wei.Gongao()),
    JIANXIONG = register("jianxiong", new Wei.Jianxiong()),
    JUEQING = register("jueqing", new Wei.Jueqing()),
    LUOSHEN = register("luoshen", new Wei.Luoshen()),
    QINGGUO = register("qingguo", new Wei.Qingguo()),
    LUOYI = register("luoyi", new Wei.Luoyi()),
    QICE = register("qice", new Wei.Qice()),
    QUANJI = register("quanji", new Wei.Quanji()),
    SHANZHUAN = register("shanzhuan", new Wei.Shanzhuan()),
    SHENSU = register("shensu", new Wei.Shensu()),
    YIJI = register("yiji", new Wei.Yiji()),
    //蜀
    BENXI = register("benxi", new Shu.Benxi()),
    HUOJI = register("huoji", new Shu.Huoji()),
    KANPO = register("kanpo", new Shu.Kanpo()),
    JIZHI = register("jizhi", new Shu.Jizhi()),
    KUANGGU = register("kuanggu", new Shu.Kuanggu()),
    LIEGONG = register("liegong", new Shu.Liegong()),
    LONGDAN = register("longdan", new Shu.Longdan()),
    RENDE = register("rende", new Shu.Rende()),
    TIEJI = register("tieji", new Shu.Tieji()),
    WUSHENG = register("wusheng", new Shu.Wusheng()),
    //吴
    BUQU = register("buqu", new Wu.Buqu()),
    FENYIN = register("fenyin", new Wu.Fenyin()),
    GONGXIN = register("gongxin", new Wu.Gongxin()),
    GUOSE = register("guose", new Wu.Guose()),
    LIULI = register("liuli", new Wu.Liuli()),
    LIANYING = register("lianying", new Wu.Lianying()),
    KUROU = register("kurou", new Wu.Kurou()),
    POJUN = register("pojun", new Wu.Pojun()),
    QIXI = register("qixi", new Wu.Qixi()),
    XIAOJI = register("xiaoji", new Wu.Xiaoji()),
    YINGZI = register("yingzi", new Wu.Yingzi()),
    ZHIHENG = register("zhiheng", new Wu.Zhiheng()),
    ZHIJIAN = register("zhijian", new Wu.Zhijian()),
    //群
    JIJIU = register("jijiu", new Qun.Jijiu()),
    JIUCHI = register("jiuchi", new Qun.Jiuchi()),
    JIZHAN = register("jizhan", new Qun.Jizhan()),
    LEIJI = register("leiji", new Qun.Leiji()),
    LUANJI = register("luanji", new Qun.Luanji()),
    TAOLUAN = register("taoluan", new Qun.Taoluan()),
    WEIMU = register("weimu", new Qun.Weimu()),
    MASHU = register("mashu", new Qun.Mashu()),

    FEIYING = register("feiying", new Qun.Feiying());

    public static Item register(String name, Item item) {
        Item skill = Registry.register(Registries.ITEM, new Identifier("dabaosword", name), item);
        SKILLS.add(skill);
        return skill;
    }

    public static void register() {}
}
