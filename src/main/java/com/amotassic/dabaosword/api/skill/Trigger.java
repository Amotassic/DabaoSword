package com.amotassic.dabaosword.api.skill;

public enum Trigger {
    /**为卡牌添加目标*/
    ADD_TARGET,
    /**为卡牌减少目标*/
    DROP_TARGET,
    /**当玩家使用卡牌确定选中目标后*/
    SELECT_TARGET,
    /**当玩家成为卡牌目标时*/
    BECOME_TARGET,
    /**从牌堆摸新牌（暂时未完成，因为还没有用到，先摆这儿）*/
    GET_CARD_DRAW,
    /**不因摸牌而获得牌*/
    GET_CARD_MOVE,
    /**因使用而失去牌*/
    LOSE_CARD_USE,
    /**因各种原因弃置而失去牌*/
    LOSE_CARD_DISCARD,
    /**因移动而失去牌*/
    LOSE_CARD_MOVE,
    /**卡牌即将被弃置，是否能弃牌*/
    SHOULD_DISCARD,
    /**取消伤害，最高优先级，高于buff但低于原版的伤害免疫检查。但真的会用到吗？*/
    CANCEL_DAMAGE_HIGHEST,
    /**取消伤害，高优先级，一般用于不产生消耗的装备，如藤甲、八卦阵*/
    CANCEL_DAMAGE_HIGH,
    /**取消伤害，一般优先级，一般用于技能*/
    CANCEL_DAMAGE_NORMAL,
    /**取消伤害，低优先级，一般用于卡牌，会产生消耗，如闪*/
    CANCEL_DAMAGE_LOW,
    /**取消伤害，最低优先级，用于确认已经绕过其余所有免伤造成伤害后，最后取消伤害，如绝情：造成伤害后将伤害改为无来源*/
    CANCEL_DAMAGE_LOWEST,
    /**
     * 当实体受到伤害进行伤害值结算时，于原版的盔甲减伤计算前修改伤害值
     * <p>
     * 伤害结算公式：最终伤害 = [原始伤害 x (1 + 增伤倍率和) + 固定数值加减伤] x (1 + 负增伤倍率)。
     * 修改伤害的具体数值通过{@link ExData#muls}和{@link ExData#adds}存储
     */
    MODIFY_DAMAGE,
    /**当实体受到伤害后*/
    ON_HURT,
    /**当实体受到伤害后，且伤害来源为卡牌*/
    HURT_BY_CARD,
    /**当实体死亡*/
    ON_DEATH,
    NONE
}
