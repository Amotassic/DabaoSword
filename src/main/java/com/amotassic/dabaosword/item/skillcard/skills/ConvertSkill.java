package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.card.CardItem;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.ui.PlayerInvScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.*;

/**
 * 转换卡牌的技能模板
 * 目前仅适用于将一张牌转化成另一张牌的技能
 */
public abstract class ConvertSkill extends SkillItem {
    public ConvertSkill(Settings settings) {super(settings);}

    /**筛选用来转换的卡牌*/
    public abstract Predicate<ItemStack> getConvertFilter();
    /**是否能够选择已经装备的装备牌来转换*/
    public boolean chooseEquipment() {return false;}
    /**转换的目标卡牌*/
    public abstract CardItem convert(ItemStack stack);

    @Override public boolean isActiveSkill() {return true;}

    @Override
    public void addScreenTip(Skill skill, List<Text> tips) {
        addPresetTips(skill, tips, 0, 1, 2, 3, 4);
        super.addScreenTip(skill, tips);
    }

    @Override
    public boolean activeSkill(PlayerEntity user, Skill skill) {
        if (skill.getCD() > 0) return false;
        if (count(user, getConvertFilter(), false, true, chooseEquipment()) > 0) {
            // 仅仅是为了不允许全选
            skill.setMaxSelect(64);
            openInv(user, user, user, skill.toHoverableText(), skill.stack, chooseEquipment(), false, 2);
            return true;
        }
        return false;
    }

    @Override
    public void onSlotClick(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target, int slot, int button, SlotActionType action) {
        ItemStack clicked = handler.getStack(slot);
        if (getConvertFilter().test(clicked)) {
            super.onSlotClick(handler, player, skill, target, slot, button, action);
        }
    }

    @Override
    public void onGuiClose(PlayerInvScreenHandler handler, PlayerEntity player, Skill skill, PlayerEntity target) {
        if (handler.getSelectedCount() > 0) {
            voice(player, this);
            var data = handler.toExData();
            data.forEachCard(3, (card, count) -> {
                ItemStack stack = card.toStack();
                give(player, c(stack, convert(stack)).toStack().copyWithCount(count));
            });
            data.clearCards(player);
        }
    }
}
