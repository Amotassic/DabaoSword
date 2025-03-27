package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.api.TriPredicate;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static com.amotassic.dabaosword.util.ModTools.*;

public interface ISkill extends Trinket {
    TriPredicate<LivingEntity, LivingEntity, DamageSource>
    ANY = (o, t, s) -> true,
    SELF = (o, t, s) -> o == t,
    NOT_SELF = (o, t, s) -> o != t,
    ATTACKER = (o, t, s) -> s != null && s.getAttacker() == o,
    DIRECT_ATTACKER = (o, t, s) -> s != null && s.getSource() == o,
    KILLER = (o, t, s) -> t.isDead() && (s != null && s.getAttacker() == o || t.getPrimeAdversary() == o);

    /**判断该技能是否拥有锁定技标签（只关系到其是否会被铁骑锁住）
     * @see Skill#lockOn() */
    default boolean lockOn() {return false;}

    /**是否是需要主动使用的技能，即必须通过客户端按下快捷键触发*/
    default boolean isActiveSkill() {return false;}

    /**无需指定其他玩家为目标的主动技效果
     * @return 如果生效则返回true*/
    default boolean activeSkill(PlayerEntity user, Skill skill) {return false;}

    default boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity target) {return false;}

    @Override
    default void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && !equipped(stack)) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.translatable("dabaosword.entity.equip", entity.getDisplayName(), stack.toHoverableText())
            ));
            setEquipped(stack, true);
        }
    }

    default boolean equipped(ItemStack stack) {return getOrCreateNbt(stack).contains("equipped");}

    default void setEquipped(ItemStack stack, boolean equipped) {
        NbtCompound nbt = getOrCreateNbt(stack);
        if (equipped) nbt.putBoolean("equipped", true);
        else nbt.remove("equipped");
        setNbt(stack, nbt);
    }

    @Override
    default void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Skill skill = s(stack);
        if (skill.lockOn() || !entity.hasStatusEffect(ModItems.TIEJI)) tickSkill(skill, entity);
        if (entity.getWorld() instanceof ServerWorld world) {
            int cd = skill.getCD(); //世界时间除以20取余为0时，技能内置CD减一秒
            if (cd > 0 && world.getTime() % 20 == 0) {
                if (entity instanceof PlayerEntity player && player.getAbilities().creativeMode) skill.setCD(0);
                else skill.setCD(cd - 1);
            }
        }
    }

    default void tickSkill(Skill skill, LivingEntity entity) {}

    /**当玩家点击技能打开的GUI中的物品后，会发生的操作逻辑，会影响玩家是否能选取卡牌。如果需要实现自定义的选取逻辑，请重写这个方法*/
    default void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
        if (action == SlotActionType.PICKUP) { //左键点击+1，右键点击-1
            if (button == 0) handler.addClick(slot, 1);
            if (button == 1) handler.dropClick(slot, 1);
        } else if (action == SlotActionType.QUICK_MOVE) { //shift+左键选一组，shift+右键取消选一组
            if (button == 0) handler.addClick(slot, 99);
            if (button == 1) handler.setClick(slot, 0);
        } else if (action == SlotActionType.SWAP && 0 <= button && button < 9) { //数字键按下几就选几个
            handler.setClick(slot, button + 1);
        }
    }

    /**当选牌GUI被关闭后，即玩家确认选牌后，需要处理选牌的逻辑*/
    default void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {}

    /**这个方法是用于在子类重写，以此快速生成一个带有参数的方法。
     * <p>
     * 加上{@link SkillInfo}注解后，子类的方法可以随意改名，但是这里该有的参数一个也不能少！*/
    @SuppressWarnings("unused")
    default int skillPattern(LivingEntity user, LivingEntity target, Skill skill, ExData data) {return 0;}

    /**在玩家打中生物实体后，但还没有进行伤害结算时触发*/
    default void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {}

    default int getExtraReach(LivingEntity entity, Skill skill) {return 0;}

    default int getDefend(LivingEntity entity, Skill skill) {return 0;}

    /**摸牌阶段开始时触发
     * @return 摸牌阶段多摸牌的数量，返回负值就减少摸牌数，若返回值小于等于-114，直接取消摸牌。*/
    default int onDrawPhase(PlayerEntity player, Skill skill) {return 0;}

}
