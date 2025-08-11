package com.amotassic.dabaosword.entity;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.amotassic.dabaosword.item.card.CardItem.onUse;
import static com.amotassic.dabaosword.util.ModTools.*;

public class UseCardGoal extends Goal {
    private final MobEntity mob;

    public UseCardGoal(MobEntity mob) {this.mob = mob;}

    @Override
    public boolean canStart() {return !getCards().isEmpty();}

    private List<ItemStack> getCards() {
        List<ItemStack> cards = new ArrayList<>();
        ItemStack main = mob.getMainHandStack();
        ItemStack off = mob.getOffHandStack();
        if (isCard(main)) cards.add(main);
        if (isCard(off)) cards.add(off);
        return cards;
    }

    @Override
    public boolean shouldRunEveryTick() {return true;}

    @Override
    public void tick() {
        if (mob.getWorld().getTime() % 20 != 0) return;
        for (ItemStack card : getCards()) {
            if (isEquipment.test(card)) onUse(mob, card, null, mob);
            if (isSha.test(card) || card.isOf(ModItems.SHAN) || card.isOf(ModItems.WUXIE)) continue;
            if (card.isOf(ModItems.WUGU) || card.isOf(ModItems.TAOYUAN)) card.setCount(0);
            if (card.isOf(ModItems.PEACH)) {
                if (mob.getHealth() > mob.getMaxHealth() - 5) continue;
                onUse(mob, card, null, mob);
            }
            if (card.isOf(ModItems.JIU)) {
                if (mob.hasStatusEffect(StatusEffects.STRENGTH)) continue;
                onUse(mob, card, null, mob);
            }
            if (card.isOf(ModItems.WUZHONG)) onUse(mob, card, null, mob);
            LivingEntity target = mob.getTarget();
            if (target == null) continue;
            if (card.isOf(ModItems.FIRE_ATTACK) || card.isOf(ModItems.WANJIAN)) {
                mob.getLookControl().lookAt(target);
                onUse(mob, card, null, mob);
            }
            if (c(card).askForWuxie()) {
                if (mob.distanceTo(target) > 5) continue;
                onUse(mob, card, null, target);
            }
        }
    }
}
