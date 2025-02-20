package com.amotassic.dabaosword.util;

import com.amotassic.dabaosword.api.config.Config;

public class ModConfig {
    private static final String ID = "dabaosword";
    private static final String GAME = "game";
    private static final String MISC = "misc";

    @Config(config = ID, category = GAME, comment = "创建游戏时搜索玩家的半径（单位：方块）")
    public static int SearchRadius = 20;

    @Config(config = ID, category = GAME, comment = "游戏开始前的等待时间（单位：秒），至少为5秒")
    public static int WaitTime = 10;

    @Config(config = ID, category = GAME, comment = "若游戏中距上次得分超过该时间，则强制结束并结算（单位：秒）")
    public static int TimeOut = 300;

    @Config(config = ID, category = MISC, comment = "是否开启手杀版连续击杀播报语音")
    public static boolean KillStreak = true;

    @Config(config = ID, category = MISC, comment = "是否允许火攻破坏方块")
    public static boolean FireAttackBreaksBlock = false;

    @Config(config = ID, category = MISC, comment = "是否开启下落攻击（就是原神那个）")
    public static boolean FallingAttack = false;
}
