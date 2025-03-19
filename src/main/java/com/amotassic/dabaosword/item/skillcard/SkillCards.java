package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.skills.Qun;
import com.amotassic.dabaosword.item.skillcard.skills.Shu;
import com.amotassic.dabaosword.item.skillcard.skills.Wei;
import com.amotassic.dabaosword.item.skillcard.skills.Wu;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SkillCards {
    public static final List<Item> SKILLS = new ArrayList<>();
    //魏
    public static final Item
    DUANLIANG = register("duanliang", Wei.Duanliang::new),
    FANGZHU = register("fangzhu", Wei.Fangzhu::new),
    XINGSHANG = register("xingshang", Wei.Xingshang::new),
    GANGLIE = register("ganglie", Wei.Ganglie::new),
    GONGAO = register("gongao", Wei.Gongao::new),
    JIANXIONG = register("jianxiong", Wei.Jianxiong::new),
    JUEQING = register("jueqing", Wei.Jueqing::new),
    LUOSHEN = register("luoshen", Wei.Luoshen::new),
    QINGGUO = register("qingguo", Wei.Qingguo::new),
    LUOYI = register("luoyi", Wei.Luoyi::new),
    QICE = register("qice", Wei.Qice::new),
    QUANJI = register("quanji", Wei.Quanji::new),
    SHANZHUAN = register("shanzhuan", Wei.Shanzhuan::new),
    SHENSU = register("shensu", Wei.Shensu::new),
    YIJI = register("yiji", Wei.Yiji::new),
    //蜀
    BENXI = register("benxi", Shu.Benxi::new),
    HUOJI = register("huoji", Shu.Huoji::new),
    KANPO = register("kanpo", Shu.Kanpo::new),
    JIZHI = register("jizhi", Shu.Jizhi::new),
    KUANGGU = register("kuanggu", Shu.Kuanggu::new),
    LIEGONG = register("liegong", Shu.Liegong::new),
    LONGDAN = register("longdan", Shu.Longdan::new),
    RENDE = register("rende", Shu.Rende::new),
    TIEJI = register("tieji", Shu.Tieji::new),
    WUSHENG = register("wusheng", Shu.Wusheng::new),
    //吴
    BUQU = register("buqu", Wu.Buqu::new),
    FENYIN = register("fenyin", Wu.Fenyin::new),
    GONGXIN = register("gongxin", Wu.Gongxin::new),
    GUOSE = register("guose", Wu.Guose::new),
    LIANYING = register("lianying", Wu.Lianying::new),
    LIULI = register("liuli", Wu.Liuli::new),
    KUROU = register("kurou", Wu.Kurou::new),
    POJUN = register("pojun", Wu.Pojun::new),
    QIXI = register("qixi", Wu.Qixi::new),
    XIAOJI = register("xiaoji", Wu.Xiaoji::new),
    YINGZI = register("yingzi", Wu.Yingzi::new),
    ZHIHENG = register("zhiheng", Wu.Zhiheng::new),
    ZHIJIAN = register("zhijian", Wu.Zhijian::new),
    //群
    JIJIU = register("jijiu", Qun.Jijiu::new),
    JIUCHI = register("jiuchi", Qun.Jiuchi::new),
    JIZHAN = register("jizhan", Qun.Jizhan::new),
    LEIJI = register("leiji", Qun.Leiji::new),
    LUANJI = register("luanji", Qun.Luanji::new),
    TAOLUAN = register("taoluan", Qun.Taoluan::new),
    WEIMU = register("weimu", Qun.Weimu::new),
    MASHU = register("mashu", Qun.Mashu::new),

    FEIYING = register("feiying", Qun.Feiying::new);

    public static Item register(String name, Function<Item.Settings, Item> factory) {
        Item skill = ModItems.register(name, factory, 1);
        SKILLS.add(skill);
        return skill;
    }

    public static void register() {}

}
