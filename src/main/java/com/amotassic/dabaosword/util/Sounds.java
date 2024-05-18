package com.amotassic.dabaosword.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {
    public static final SoundEvent NAHIDA1 = Sounds.register("nahida1");
    public static final SoundEvent NAHIDA2 = Sounds.register("nahida2");
    public static final SoundEvent NAHIDA3 = Sounds.register("nahida3");
    public static final SoundEvent FALL1 = Sounds.register("falling_attack1");
    public static final SoundEvent FALL2 = Sounds.register("falling_attack2");
    public static final SoundEvent LIEGONG1 = Sounds.register("liegong1");
    public static final SoundEvent LIEGONG2 = Sounds.register("liegong2");
    public static final SoundEvent GONGAO1 = Sounds.register("gongao1");
    public static final SoundEvent GONGAO2 = Sounds.register("gongao2");
    public static final SoundEvent WEIZHONG = Sounds.register("weizhong");
    public static final SoundEvent BENXI1 = Sounds.register("benxi1");
    public static final SoundEvent BENXI2 = Sounds.register("benxi2");
    public static final SoundEvent LEIJI1 = Sounds.register("leiji1");
    public static final SoundEvent LEIJI2 = Sounds.register("leiji2");
    public static final SoundEvent GIFTBOX = Sounds.register("giftbox");
    public static final SoundEvent KANPO1 = Sounds.register("kanpo1");
    public static final SoundEvent KANPO2 = Sounds.register("kanpo2");
    public static final SoundEvent GUOSE1 = Sounds.register("guose1");
    public static final SoundEvent GUOSE2 = Sounds.register("guose2");
    public static final SoundEvent LIULI1 = Sounds.register("liuli1");
    public static final SoundEvent LIULI2 = Sounds.register("liuli2");
    public static final SoundEvent JUEQING1 = Sounds.register("jueqing1");
    public static final SoundEvent JUEQING2 = Sounds.register("jueqing2");
    public static final SoundEvent LUANJI1 = Sounds.register("luanji1");
    public static final SoundEvent LUANJI2 = Sounds.register("luanji2");
    public static final SoundEvent LUANJI3 = Sounds.register("luanji3");
    public static final SoundEvent LUANJI4 = Sounds.register("luanji4");
    public static final SoundEvent KUROU1 = Sounds.register("kurou1");
    public static final SoundEvent KUROU2 = Sounds.register("kurou2");
    public static final SoundEvent JIZHI1 = Sounds.register("jizhi1");
    public static final SoundEvent JIZHI2 = Sounds.register("jizhi2");
    public static final SoundEvent QICE1 = Sounds.register("qice1");
    public static final SoundEvent QICE2 = Sounds.register("qice2");
    public static final SoundEvent LUOYI1 = Sounds.register("luoyi1");
    public static final SoundEvent LUOYI2 = Sounds.register("luoyi2");
    public static final SoundEvent HUOJI1 = Sounds.register("huoji1");
    public static final SoundEvent HUOJI2 = Sounds.register("huoji2");
    public static final SoundEvent QUANJI1 = Sounds.register("quanji1");
    public static final SoundEvent QUANJI2 = Sounds.register("quanji2");
    public static final SoundEvent ZILI1 = Sounds.register("zili1");
    public static final SoundEvent ZILI2 = Sounds.register("zili2");
    public static final SoundEvent PAIYI1 = Sounds.register("paiyi1");
    public static final SoundEvent PAIYI2 = Sounds.register("paiyi2");
    public static final SoundEvent PAIYI3 = Sounds.register("paiyi3");
    public static final SoundEvent PAIYI4 = Sounds.register("paiyi4");
    public static final SoundEvent YIJI1 = Sounds.register("yiji1");
    public static final SoundEvent YIJI2 = Sounds.register("yiji2");
    public static final SoundEvent TAOLUAN1 = Sounds.register("taoluan1");
    public static final SoundEvent TAOLUAN2 = Sounds.register("taoluan2");
    public static final SoundEvent POJUN1 = Sounds.register("pojun1");
    public static final SoundEvent POJUN2 = Sounds.register("pojun2");
    public static final SoundEvent KUANGGU1 = Sounds.register("kuanggu1");
    public static final SoundEvent KUANGGU2 = Sounds.register("kuanggu2");


    public static final SoundEvent BINGLIANG = Sounds.register("bingliang");
    public static final SoundEvent GUOHE = Sounds.register("guohe");
    public static final SoundEvent HUOGONG = Sounds.register("huogong");
    public static final SoundEvent JIEDAO = Sounds.register("jiedao");
    public static final SoundEvent JIU = Sounds.register("jiu");
    public static final SoundEvent JUEDOU = Sounds.register("juedou");
    public static final SoundEvent LEBU = Sounds.register("lebu");
    public static final SoundEvent RECOVER = Sounds.register("recover");
    public static final SoundEvent SHAN = Sounds.register("shan");
    public static final SoundEvent SHUNSHOU = Sounds.register("shunshou");
    public static final SoundEvent TAOYUAN = Sounds.register("taoyuan");
    public static final SoundEvent TIESUO = Sounds.register("tiesuo");
    public static final SoundEvent WANJIAN = Sounds.register("wanjian");
    public static final SoundEvent WUXIE = Sounds.register("wuxie");
    public static final SoundEvent WUZHONG = Sounds.register("wuzhong");
    public static final SoundEvent NANMAN = Sounds.register("nanman");
    public static final SoundEvent SHA = Sounds.register("sha");
    public static final SoundEvent SHA_FIRE = Sounds.register("sha_fire");
    public static final SoundEvent SHA_THUNDER = Sounds.register("sha_thunder");

    public static void sound() {}
    public static SoundEvent register(String name){
        Identifier identifier = new Identifier("dabaosword",name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
