package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.DabaoSword;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;

import java.util.function.ToIntFunction;

public class Gamerule {
    public static final GameRule<Boolean> CARD_PILE_HUNGERLESS =
            registerBoolean("card_pile_hungerless", GameRuleCategory.MISC, false);
    public static final GameRule<Boolean> CLEAR_CARDS_AFTER_DEATH =
            registerBoolean("clear_cards_after_death", GameRuleCategory.MISC, true);
    public static final GameRule<Integer> GIVE_CARD_INTERVAL = registerInteger(
            "give_card_interval", GameRuleCategory.MISC, 60, 1);
    public static final GameRule<Integer> CHANGE_SKILL_INTERVAL = registerInteger(
            "change_skill_interval", GameRuleCategory.MISC, 300, -1);
    public static final GameRule<Boolean> ENABLE_CARDS_LIMIT =
            registerBoolean("enable_cards_limit", GameRuleCategory.MISC, true);

    private static GameRule<Boolean> registerBoolean(String id, GameRuleCategory category, boolean defaultValue) {
        return register(id, category, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean, Codec.BOOL, b -> b ? 1 : 0, defaultValue);
    }

    private static GameRule<Integer> registerInteger(String id, GameRuleCategory category, int defaultValue, int min) {
        int max = Integer.MAX_VALUE;
        return register(id, category, GameRuleType.INT, IntegerArgumentType.integer(min, max), GameRuleTypeVisitor::visitInteger, Codec.intRange(min, max), i -> i, defaultValue);
    }

    private static <T> GameRule<T> register(String id, GameRuleCategory category, GameRuleType type, ArgumentType<T> argument, GameRules.VisitorCaller<T> caller, Codec<T> codec, ToIntFunction<T> function, T defaultValue) {
        return Registry.register(BuiltInRegistries.GAME_RULE, DabaoSword.id(id), new GameRule<>(category, type, argument, caller, codec, function, defaultValue, FeatureFlagSet.of()));
    }

    public static void registerGamerules() {}
}
