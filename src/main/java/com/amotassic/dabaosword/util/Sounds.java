package com.amotassic.dabaosword.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class Sounds {
    public static final SoundEvent
    NAHIDA = register("nahida"),
    FENYIN = register("fenyin"),
    JIJIU = register("jijiu"),
    JIUCHI = register("jiuchi"),
    JIANXIONG = register("jianxiong"),
    JIZHAN = register("jizhan"),
    YINGZI = register("yingzi"),
    WUSHENG = register("wusheng"),
    WEIMU = register("weimu"),
    SHENSU = register("shensu"),
    LIANYING = register("lianying"),
    XIAOJI = register("xiaoji"),
    LET_ME_CC = register("letmecc"),
    LONGDAN = register("longdan"),
    GONGXIN = register("gongxin"),
    ZHIJIAN = register("zhijian"),
    SHANZHUAN = register("shanzhuan"),
    RENDE = register("rende"),
    ZHIHENG = register("zhiheng"),
    BUQU = register("buqu"),
    FALL1 = register("falling_attack1"),
    FALL2 = register("falling_attack2"),
    TIEJI = register("tieji"),
    GANGLIE = register("ganglie"),
    FANGZHU = register("fangzhu"),
    XINGSHANG = register("xingshang"),
    BBJI = register("bbji"),
    XUYOU = register("xuyou"),
    DUANLIANG = register("duanliang"),
    LUOSHEN = register("luoshen"),
    QIXI = register("qixi"),
    QINGGUO = register("qingguo"),
    LIEGONG = register("liegong"),
    GONGAO = register("gongao"),
    WEIZHONG = register("weizhong"),
    BENXI = register("benxi"),
    LEIJI = register("leiji"),
    GIFTBOX = register("giftbox"),
    KANPO = register("kanpo"),
    GUOSE = register("guose"),
    LIULI = register("liuli"),
    JUEQING = register("jueqing"),
    LUANJI = register("luanji"),
    KUROU = register("kurou"),
    JIZHI = register("jizhi"),
    QICE = register("qice"),
    LUOYI = register("luoyi"),
    HUOJI = register("huoji"),
    QUANJI = register("quanji"),
    ZILI = register("zili"),
    PAIYI = register("paiyi"),
    YIJI = register("yiji"),
    TAOLUAN = register("taoluan"),
    POJUN = register("pojun"),
    KUANGGU = register("kuanggu"),

    BAGUA = register("bagua"),
    BAIYIN = register("baiyin"),
    CIXIONG = register("cixiong"),
    FANGTIAN = register("fangtian"),
    GUANSHI = register("guanshi"),
    GUDING = register("guding"),
    HANBING = register("hanbing"),
    LIANNU = register("liannu"),
    QILIN = register("qilin"),
    QINGGANG = register("qinggang"),
    QINGLONG = register("qinglong"),
    RENWANG = register("renwang"),
    TENGJIA1 = register("tengjia1"),
    TENGJIA2 = register("tengjia2"),
    ZHANGBA = register("zhangba"),
    ZHUQUE = register("zhuque"),

    BINGLIANG = register("bingliang"),
    GUOHE = register("discard"),
    HUOGONG = register("huogong"),
    JIEDAO = register("jiedao"),
    JIU = register("jiu"),
    JUEDOU = register("juedou"),
    LEBU = register("too_happy"),
    RECOVER = register("peach"),
    SHAN = register("shan"),
    SHANDIAN = register("shandian"),
    SHUNSHOU = register("steal"),
    TAOYUAN = register("taoyuan"),
    TIESUO = register("tiesuo"),
    WANJIAN = register("wanjian"),
    WUGU = register("wugu"),
    WUXIE = register("wuxie"),
    WUZHONG = register("wuzhong"),
    NANMAN = register("nanman"),
    SHA = register("sha"),
    SHA_FIRE = register("fire_sha"),
    SHA_THUNDER = register("thunder_sha");

    static {
        register("diankuang"); register("wushuang"); register("win"); register("kill1"); register("kill2"); register("kill3"); register("kill4"); register("kill5"); register("kill6"); register("kill7");
    }

    public static void sound() {}
    public static SoundEvent register(String name){
        Identifier identifier = new Identifier("dabaosword",name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
