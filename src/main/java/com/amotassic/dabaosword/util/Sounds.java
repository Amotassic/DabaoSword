package com.amotassic.dabaosword.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {
    public static final SoundEvent NAHIDA = register("nahida");
    public static final SoundEvent SHENSU = register("shensu");
    public static final SoundEvent LIANYING = register("lianying");
    public static final SoundEvent XIAOJI = register("xiaoji");
    public static final SoundEvent LET_ME_CC = register("letmecc");
    public static final SoundEvent LONGDAN = register("longdan");
    public static final SoundEvent GONGXIN = register("gongxin");
    public static final SoundEvent ZHIJIAN = register("zhijian");
    public static final SoundEvent SHANZHUAN = register("shanzhuan");
    public static final SoundEvent RENDE = register("rende");
    public static final SoundEvent ZHIHENG = register("zhiheng");
    public static final SoundEvent BUQU = register("buqu");
    public static final SoundEvent FALL1 = register("falling_attack1");
    public static final SoundEvent FALL2 = register("falling_attack2");
    public static final SoundEvent TIEJI = register("tieji");
    public static final SoundEvent GANGLIE = register("ganglie");
    public static final SoundEvent FANGZHU = register("fangzhu");
    public static final SoundEvent XINGSHANG = register("xingshang");
    public static final SoundEvent BBJI = register("bbji");
    public static final SoundEvent XUYOU = register("xuyou");
    public static final SoundEvent DUANLIANG = register("duanliang");
    public static final SoundEvent LUOSHEN = register("luoshen");
    public static final SoundEvent QIXI = register("qixi");
    public static final SoundEvent QINGGUO = register("qingguo");
    public static final SoundEvent LIEGONG = register("liegong");
    public static final SoundEvent GONGAO = register("gongao");
    public static final SoundEvent WEIZHONG = register("weizhong");
    public static final SoundEvent BENXI = register("benxi");
    public static final SoundEvent LEIJI = register("leiji");
    public static final SoundEvent GIFTBOX = register("giftbox");
    public static final SoundEvent KANPO = register("kanpo");
    public static final SoundEvent GUOSE = register("guose");
    public static final SoundEvent LIULI = register("liuli");
    public static final SoundEvent JUEQING = register("jueqing");
    public static final SoundEvent LUANJI = register("luanji");
    public static final SoundEvent KUROU = register("kurou");
    public static final SoundEvent JIZHI = register("jizhi");
    public static final SoundEvent QICE = register("qice");
    public static final SoundEvent LUOYI = register("luoyi");
    public static final SoundEvent HUOJI = register("huoji");
    public static final SoundEvent QUANJI = register("quanji");
    public static final SoundEvent ZILI = register("zili");
    public static final SoundEvent PAIYI = register("paiyi");
    public static final SoundEvent YIJI = register("yiji");
    public static final SoundEvent TAOLUAN = register("taoluan");
    public static final SoundEvent POJUN = register("pojun");
    public static final SoundEvent KUANGGU = register("kuanggu");

    public static final SoundEvent BAGUA = register("bagua");
    public static final SoundEvent BAIYIN = register("baiyin");
    public static final SoundEvent FANGTIAN = register("fangtian");
    public static final SoundEvent GUDING = register("guding");
    public static final SoundEvent HANBING = register("hanbing");
    public static final SoundEvent QINGGANG = register("qinggang");
    public static final SoundEvent QINGLONG = register("qinglong");
    public static final SoundEvent TENGJIA1 = register("tengjia1");
    public static final SoundEvent TENGJIA2 = register("tengjia2");

    public static final SoundEvent BINGLIANG = register("bingliang");
    public static final SoundEvent GUOHE = register("guohe");
    public static final SoundEvent HUOGONG = register("huogong");
    public static final SoundEvent JIEDAO = register("jiedao");
    public static final SoundEvent JIU = register("jiu");
    public static final SoundEvent JUEDOU = register("juedou");
    public static final SoundEvent LEBU = register("lebu");
    public static final SoundEvent RECOVER = register("recover");
    public static final SoundEvent SHAN = register("shan");
    public static final SoundEvent SHUNSHOU = register("shunshou");
    public static final SoundEvent TAOYUAN = register("taoyuan");
    public static final SoundEvent TIESUO = register("tiesuo");
    public static final SoundEvent WANJIAN = register("wanjian");
    public static final SoundEvent WUXIE = register("wuxie");
    public static final SoundEvent WUZHONG = register("wuzhong");
    public static final SoundEvent NANMAN = register("nanman");
    public static final SoundEvent SHA = register("sha");
    public static final SoundEvent SHA_FIRE = register("sha_fire");
    public static final SoundEvent SHA_THUNDER = register("sha_thunder");

    public static void sound() {}
    public static SoundEvent register(String name){
        Identifier identifier = new Identifier("dabaosword",name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
