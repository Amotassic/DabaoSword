package com.amotassic.dabaosword.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class Gamerule {
    public static final GameRules.Key<GameRules.BooleanRule> FIRE_ATTACK_BREAKS_BLOCK =
            GameRuleRegistry.register("fire_attack_breaks_block", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static void registerGamerules() {}
}
