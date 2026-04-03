package com.amotassic.dabaosword.entity;

import com.amotassic.dabaosword.item.ModItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.amotassic.dabaosword.item.card.CardItem.onUse;
import static com.amotassic.dabaosword.util.ModTools.*;

public class UseCardGoal extends Goal {
    private final Mob mob;

    public UseCardGoal(Mob mob) {this.mob = mob;}

    @Override
    public boolean canUse() {return !getCards().isEmpty();}

    private List<ItemStack> getCards() {
        List<ItemStack> cards = new ArrayList<>();
        ItemStack main = mob.getMainHandItem();
        ItemStack off = mob.getOffhandItem();
        if (isCard(main)) cards.add(main);
        if (isCard(off)) cards.add(off);
        return cards;
    }

    @Override
    public boolean requiresUpdateEveryTick() {return true;}

    @Override
    public void tick() {
        if (mob.level().getGameTime() % 20 != 0) return;
        for (ItemStack card : getCards()) {
            if (isEquipment.test(card)) onUse(mob, card, null, mob);
            if (isSha.test(card) || card.is(ModItems.SHAN) || card.is(ModItems.WUXIE)) continue;
            if (card.is(ModItems.WUGU) || card.is(ModItems.TAOYUAN)) card.setCount(0);
            if (card.is(ModItems.PEACH)) {
                if (mob.getHealth() > mob.getMaxHealth() - 5) continue;
                onUse(mob, card, null, mob);
            }
            if (card.is(ModItems.JIU)) {
                if (mob.hasEffect(MobEffects.STRENGTH)) continue;
                onUse(mob, card, null, mob);
            }
            if (card.is(ModItems.WUZHONG)) onUse(mob, card, null, mob);
            LivingEntity target = mob.getTarget();
            if (target == null) continue;
            if (card.is(ModItems.FIRE_ATTACK) || card.is(ModItems.WANJIAN)) {
                mob.getLookControl().setLookAt(target);
                onUse(mob, card, null, mob);
            }
            if (c(card).askForWuxie()) {
                if (mob.distanceTo(target) > 5) continue;
                onUse(mob, card, null, target);
            }
        }
    }
}
