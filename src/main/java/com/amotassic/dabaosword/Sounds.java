package com.amotassic.dabaosword;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {
    public static final SoundEvent NAHIDA1 = Sounds.register("nahida1");
    public static final SoundEvent NAHIDA2 = Sounds.register("nahida2");
    public static final SoundEvent NAHIDA3 = Sounds.register("nahida3");
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
    public static final SoundEvent TIESUO = Sounds.register("tiesuo");
    public static final SoundEvent WANJIAN = Sounds.register("wanjian");
    public static final SoundEvent WUXIE = Sounds.register("wuxie");
    public static final SoundEvent WUZHONG = Sounds.register("wuzhong");

    public static void sound() {}
    public static SoundEvent register(String name){
        Identifier identifier = new Identifier("dabaosword",name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
