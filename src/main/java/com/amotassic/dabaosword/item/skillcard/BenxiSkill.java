package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static com.amotassic.dabaosword.util.ModTools.hasTrinket;
import static com.amotassic.dabaosword.util.ModTools.noTieji;

public class BenxiSkill extends SkillItem {
    public BenxiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noLongHand(player) && noTieji(entity)) {
            if (stack.get(ModItems.TAGS) == null) stack.set(ModItems.TAGS, 0);
            else {
                int benxi = Objects.requireNonNull(stack.get(ModItems.TAGS));
                if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 2,false,false,true));
                } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 1,false,false,true));
                } else if (benxi != 0) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi - 1,false,false,true));
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    private boolean noLongHand(PlayerEntity player) {
        return player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }
}
