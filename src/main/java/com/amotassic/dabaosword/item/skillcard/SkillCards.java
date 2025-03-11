package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.skillcard.skills.Qun;
import com.amotassic.dabaosword.item.skillcard.skills.Shu;
import com.amotassic.dabaosword.item.skillcard.skills.Wei;
import com.amotassic.dabaosword.item.skillcard.skills.Wu;
import net.minecraft.item.Item;

import static com.amotassic.dabaosword.item.ModItems.skill;

public class SkillCards {
    //魏
    public static final Item DUANLIANG = skill("duanliang", Wei.Duanliang::new);
    public static final Item FANGZHU = skill("fangzhu", Wei.Fangzhu::new);
    public static final Item XINGSHANG = skill("xingshang", Wei.Xingshang::new);
    public static final Item GANGLIE = skill("ganglie", Wei.Ganglie::new);
    public static final Item GONGAO = skill("gongao", Wei.Gongao::new);
    public static final Item JIANXIONG = skill("jianxiong", Wei.Jianxiong::new);
    public static final Item JUEQING = skill("jueqing", Wei.Jueqing::new);
    public static final Item LUOSHEN = skill("luoshen", Wei.Luoshen::new);
    public static final Item QINGGUO = skill("qingguo", Wei.Qingguo::new);
    public static final Item LUOYI = skill("luoyi", Wei.Luoyi::new);
    public static final Item QICE = skill("qice", Wei.Qice::new);
    public static final Item QUANJI = skill("quanji", Wei.Quanji::new);
    public static final Item SHANZHUAN = skill("shanzhuan", Wei.Shanzhuan::new);
    public static final Item SHENSU = skill("shensu", Wei.Shensu::new);
    public static final Item YIJI = skill("yiji", Wei.Yiji::new);
    //蜀
    public static final Item BENXI = skill("benxi", Shu.Benxi::new);
    public static final Item HUOJI = skill("huoji", Shu.Huoji::new);
    public static final Item KANPO = skill("kanpo", Shu.Kanpo::new);
    public static final Item JIZHI = skill("jizhi", Shu.Jizhi::new);
    public static final Item KUANGGU = skill("kuanggu", Shu.Kuanggu::new);
    public static final Item LIEGONG = skill("liegong", Shu.Liegong::new);
    public static final Item LONGDAN = skill("longdan", Shu.Longdan::new);
    public static final Item RENDE = skill("rende", Shu.Rende::new);
    public static final Item TIEJI = skill("tieji", Shu.Tieji::new);
    public static final Item WUSHENG = skill("wusheng", Shu.Wusheng::new);
    //吴
    public static final Item BUQU = skill("buqu", Wu.Buqu::new);
    public static final Item FENYIN = skill("fenyin", Wu.Fenyin::new);
    public static final Item GONGXIN = skill("gongxin", Wu.Gongxin::new);
    public static final Item GUOSE = skill("guose", Wu.Guose::new);
    public static final Item LIANYING = skill("lianying", Wu.Lianying::new);
    public static final Item LIULI = skill("liuli", Wu.Liuli::new);
    public static final Item KUROU = skill("kurou", Wu.Kurou::new);
    public static final Item POJUN = skill("pojun", Wu.Pojun::new);
    public static final Item QIXI = skill("qixi", Wu.Qixi::new);
    public static final Item XIAOJI = skill("xiaoji", Wu.Xiaoji::new);
    public static final Item YINGZI = skill("yingzi", Wu.Yingzi::new);
    public static final Item ZHIHENG = skill("zhiheng", Wu.Zhiheng::new);
    public static final Item ZHIJIAN = skill("zhijian", Wu.Zhijian::new);
    //群
    public static final Item JIJIU = skill("jijiu", Qun.Jijiu::new);
    public static final Item JIUCHI = skill("jiuchi", Qun.Jiuchi::new);
    public static final Item JIZHAN = skill("jizhan", Qun.Jizhan::new);
    public static final Item LEIJI = skill("leiji", Qun.Leiji::new);
    public static final Item LUANJI = skill("luanji", Qun.Luanji::new);
    public static final Item TAOLUAN = skill("taoluan", Qun.Taoluan::new);
    public static final Item WEIMU = skill("weimu", Qun.Weimu::new);
    public static final Item MASHU = skill("mashu", Qun.Mashu::new);

    public static final Item FEIYING = skill("feiying", Qun.Feiying::new);

    public static void registerSkills() {}

}
