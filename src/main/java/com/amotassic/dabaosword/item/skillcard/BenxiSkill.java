package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BenxiSkill extends SkillItem implements ModTools {
    public BenxiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getNbt() != null) {
            int benxi = stack.getNbt().getInt("benxi");
            tooltip.add(Text.of("奔袭：" + benxi));
        }
        tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip1").formatted(Formatting.RED));
        tooltip.add(Text.translatable("item.dabaosword.benxi.tooltip2").formatted(Formatting.RED));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noLongHand(player)) {
            NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() == null) {nbt.putInt("benxi", 0); stack.setNbt(nbt);}
            else {
                int benxi = stack.getNbt().getInt("benxi");
                if (benxi > 0) {
                    if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 2,false,false,true));
                    } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi + 1,false,false,true));
                    } else {player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,benxi - 1,false,false,true));}
                }
                if (benxi == 0) {
                    if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,2,false,false,true));
                    } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                        player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1,false,false,true));
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    private boolean noLongHand(PlayerEntity player) {
        return player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }
}
