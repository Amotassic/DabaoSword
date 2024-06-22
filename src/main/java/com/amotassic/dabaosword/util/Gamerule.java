package com.amotassic.dabaosword.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class Gamerule {
    public static final GameRules.Key<GameRules.BooleanRule> FIRE_ATTACK_BREAKS_BLOCK =
            GameRuleRegistry.register("fire_attack_breaks_block", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> CARD_PILE_HUNGERLESS =
            GameRuleRegistry.register("card_pile_hungerless", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> CLEAR_CARDS_AFTER_DEATH =
            GameRuleRegistry.register("clear_cards_after_death", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.IntRule> GIVE_CARD_INTERVAL = GameRuleRegistry.register(
            "give_card_interval", GameRules.Category.MISC, GameRuleFactory.createIntRule(60,1));
    public static final GameRules.Key<GameRules.IntRule> CHANGE_SKILL_INTERVAL = GameRuleRegistry.register(
            "change_skill_interval", GameRules.Category.MISC, GameRuleFactory.createIntRule(300,-1));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_FALLING_ATTACK =
            GameRuleRegistry.register("enable_falling_attack", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CARDS_LIMIT =
            GameRuleRegistry.register("enable_cards_limit", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static void registerGamerules() {}
}
