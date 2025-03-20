package com.amotassic.dabaosword.api.skill;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 一个用于封装技能物品堆的类，省去了以原版物品堆为基础实现技能的麻烦。
 * 即使构造函数传入的物品堆不是技能物品，也会将其封装为{@link ModItems#EMPTY_SKILL}，不至于出现类转换异常。
 * @author Amotassic
 */
public final class Skill {
    public final ItemStack stack;
    public final ISkill item;

    public Skill(ItemStack skill) {
        this.stack = skill;
        this.item = skill.getItem() instanceof ISkill iSkill ? iSkill : ModItems.EMPTY_SKILL;
    }

    public boolean lockOn() {
        if (item.isActiveSkill()) return false;
        return this.item.lockOn();
    }

    public boolean isActiveSkill() {return this.item.isActiveSkill();}

    public List<SkillExecutor> data() {
        return SkillCards.SKILL_MAP.getOrDefault(stack.getItem(), List.of());
    }

    public boolean activeSkill(PlayerEntity user, Skill skill) {return item.activeSkill(user, skill);}
    public boolean activeSkill(PlayerEntity user, Skill skill, LivingEntity target) {return item.activeSkill(user, skill, target);}

    public int getCD() {return getNbt().getInt("cooldown");}

    public void setCD(int seconds) { //设置物品的内置冷却时间
        var nbt = getNbt();
        nbt.putInt("cooldown", seconds);
        setNbt(nbt);
    }

    public int getTag() {return getNbt().getInt("tags");}

    public void setTag(int value) { //设置物品的标签的数量
        var nbt = getNbt();
        nbt.putInt("tags", value);
        setNbt(nbt);
    }

    public int getMinSelect() {
        var nbt = getNbt();
        if (!nbt.contains("minSelect")) return -1;
        return nbt.getInt("minSelect");
    }
    public void setMinSelect(int value) { //设置打开GUI时后需要选择物品的最小数量
        var nbt = getNbt();
        nbt.putInt("minSelect", value);
        setNbt(nbt);
    }

    public int getMaxSelect() {
        var nbt = getNbt();
        if (!nbt.contains("maxSelect")) return Integer.MAX_VALUE;
        return nbt.getInt("maxSelect");
    }
    public void setMaxSelect(int value) { //设置打开GUI时后需要选择物品的最大数量
        var nbt = getNbt();
        nbt.putInt("maxSelect", value);
        setNbt(nbt);
    }

    /**若需要同时限制技能选取卡牌的最大值和最小值，调用这个方法更方便*/
    public void setShouldSelect(int min, int max) {setMinSelect(min); setMaxSelect(max);}

    public boolean isEmpty() {return stack.isEmpty();}

    public Text toHoverableText() {return stack.toHoverableText();}

    public NbtCompound getNbt() {return stack.getOrCreateNbt();}

    public void setNbt(NbtCompound nbt) {stack.setNbt(nbt);}

}
