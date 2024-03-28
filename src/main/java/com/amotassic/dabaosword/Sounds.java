package com.amotassic.dabaosword;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {
    public static final SoundEvent POJUN1 = Sounds.register("pojun1");
    public static final SoundEvent POJUN2 = Sounds.register("pojun2");

    public static void sound() {}
    public static SoundEvent register(String name){
        Identifier identifier = new Identifier("dabaosword",name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
