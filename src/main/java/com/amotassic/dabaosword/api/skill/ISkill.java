package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.api.TriPredicate;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.*;

public interface ISkill extends Trinket {
    TriPredicate<LivingEntity, LivingEntity, DamageSource>
    ANY = (o, t, s) -> true,
    SELF = (o, t, s) -> o == t,
    NOT_SELF = (o, t, s) -> o != t,
    ATTACKER = (o, t, s) -> s != null && s.getEntity() == o,
    DIRECT_ATTACKER = (o, t, s) -> s != null && s.getDirectEntity() == o,
    KILLER = (o, t, s) -> t.isDeadOrDying() && (s != null && s.getEntity() == o || t.getKillCredit() == o);

    /**判断该技能是否拥有锁定技标签（只关系到其是否会被铁骑锁住）
     * @see Skill#lockOn() */
    default boolean lockOn() {return false;}

    /**是否是需要主动使用的技能，即必须通过客户端按下快捷键触发*/
    default boolean isActiveSkill() {return false;}

    /**无需指定其他玩家为目标的主动技效果
     * @return 如果生效则返回true*/
    default boolean activeSkill(Player user, Skill skill) {return false;}

    default boolean activeSkill(Player user, Skill skill, LivingEntity target) {return false;}

    @Override
    default void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.level() instanceof ServerLevel world && !equipped(stack)) {
            world.players().forEach(player -> player.sendSystemMessage(
                    Component.translatable("dabaosword.entity.equip", entity.getDisplayName(), stack.getDisplayName())
            ));
            setEquipped(stack, true);
        }
    }

    default boolean equipped(ItemStack stack) {return getOrCreateNbt(stack).contains("equipped");}

    default void setEquipped(ItemStack stack, boolean equipped) {
        var nbt = getOrCreateNbt(stack);
        if (equipped) nbt.putBoolean("equipped", true);
        else nbt.remove("equipped");
        setNbt(stack, nbt);
    }

    @Override
    default void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof SkillItem && entity.entityTags().contains("duanchang")) return;
        Skill skill = s(stack);
        if (skill.lockOn() || !entity.hasEffect(ModItems.TIEJI)) tickSkill(skill, entity);
        if (entity.level() instanceof ServerLevel world) {
            int cd = skill.getCD(); //世界时间除以20取余为0时，技能内置CD减一秒
            if (cd > 0 && world.getGameTime() % 20 == 0) {
                if (entity instanceof Player player && player.getAbilities().instabuild) skill.setCD(0);
                else skill.setCD(cd - 1);
            }
        }
    }

    /**当玩家被断肠或者非锁定技失效时，此效果将无法触发*/
    default void tickSkill(Skill skill, LivingEntity entity) {}

    default boolean shouldTickUpdate() {return false;}
    default void tickUpdateNbt(Skill skill, LivingEntity entity) {
        int tick = (int) (entity.level().getGameTime() % 20);
        var nbt = skill.getNbt();
        nbt.putInt("TickOfSecond", tick);
        skill.setNbt(nbt);
    }

    /**在打开的GUI上添加操作提示，可按需添加，默认会添加物品本身的物品提示
     * @see ISkill#addPresetTips(Skill, List, Integer...) */
    default void addScreenTip(Skill skill, List<Component> tips) {
        tips.add(skill.toHoverableText());
        if (skill.stack.getItem() instanceof SkillItem si) si.addTip(skill, tips);
    }

    /**
     * 预设的GUI界面提示信息，按需填写即可。
     * @param presetLines 可以填写的预设的信息如下：
     * <ul>
     *     <li>0：标题以及关闭GUI确定选择操作提示</li>
     *     <li>1：左键右键操作提示</li>
     *     <li>2：鼠标滚轮操作提示</li>
     *     <li>3：Shift+左/右键操作提示，若限制最大选牌数量则不显示</li>
     *     <li>4：全选（若限制最大选牌数量则不显示）、取消全选操作的提示</li>
     *     <li>5：最大可选数量、最小可选数量（如果没有限制则为空）的提示</li>
     * </ul>
     */
    default void addPresetTips(Skill skill, List<Component> tips, Integer... presetLines) {
        var preset = List.of(presetLines);
        int max = skill.getMaxSelect(); int min = skill.getMinSelect();
        for (Integer i : preset) {
            switch (i) {
                case 0 -> tips.add(Component.translatable("screen.dabaosword.title").withStyle(AQUA, BOLD).append(Component.translatable("screen.dabaosword.title_").withStyle(RED, BOLD)));
                case 1 -> tips.add(Component.translatable("screen.dabaosword.tip1").withStyle(BOLD));
                case 2 -> tips.add(Component.translatable("screen.dabaosword.tip2").withStyle(AQUA, BOLD));
                case 3 -> {
                    if (max > 100) tips.add(Component.translatable("screen.dabaosword.tip3").withStyle(BOLD));
                }
                case 4 -> {
                    MutableComponent text = max >= 100 ? Component.translatable("screen.dabaosword.ctrl_a").withStyle(AQUA, BOLD) : Component.literal("");
                    tips.add(text.append(Component.translatable("screen.dabaosword.ctrl_z").withStyle(AQUA, BOLD)));
                }
                case 5 -> {
                    MutableComponent minText = min > 0 ? Component.translatable("screen.dabaosword.min_tip", min).withStyle(RED, BOLD) : Component.literal("");
                    MutableComponent maxText = max < 100 ? Component.translatable("screen.dabaosword.max_tip", max).withStyle(RED, BOLD) : Component.literal("");
                    tips.add(minText.append(maxText));
                }
            }
        }
    }

    /**当玩家点击技能打开的GUI中的物品后，会发生的操作逻辑，会影响玩家是否能选取卡牌。如果需要实现自定义的选取逻辑，请重写这个方法*/
    default void onSlotClick(PlayerInvScreenHandler handler, Player player, Skill skill, Player target, int slot, int button, ContainerInput action) {
        if (action == ContainerInput.PICKUP) { //左键点击+1，右键点击-1
            if (button == 0) handler.addClick(slot, 1);
            if (button == 1) handler.dropClick(slot, 1);
        } else if (action == ContainerInput.QUICK_MOVE) { //shift+左键选一组，shift+右键取消选一组
            if (button == 0) handler.addClick(slot, 99);
            if (button == 1) handler.setClick(slot, 0);
        } else if (action == ContainerInput.SWAP && 0 <= button && button < 9) { //数字键按下几就选几个
            handler.setClick(slot, button + 1);
        }
    }

    /**当选牌GUI被关闭后，即玩家确认选牌后，需要处理选牌的逻辑*/
    default void onGuiClose(PlayerInvScreenHandler handler, Player player, Skill skill, Player target) {}

    /**这个方法是用于在子类重写，以此快速生成一个带有参数的方法。
     * <p>
     * 加上{@link SkillInfo}注解后，子类的方法可以随意改名，但是这里该有的参数一个也不能少！
     * @return 除了取消伤害五个触发时机的技能需要返回大于0的值，其余请直接返回0*/
    @SuppressWarnings("unused")
    default int skillPattern(LivingEntity user, LivingEntity target, Skill skill, ExData data) {return 0;}

    /**在玩家打中生物实体后，但还没有进行伤害结算时触发*/
    default void preAttack(Player player, LivingEntity target, Skill skill) {}

    default int getExtraReach(LivingEntity entity, Skill skill) {return 0;}

    default int getDefend(LivingEntity entity, Skill skill) {return 0;}

    /**摸牌阶段开始时触发
     * @return 摸牌阶段多摸牌的数量，返回负值就减少摸牌数，若返回值小于等于-114，直接取消摸牌。*/
    default int onDrawPhase(Player player, Skill skill) {return 0;}

}
