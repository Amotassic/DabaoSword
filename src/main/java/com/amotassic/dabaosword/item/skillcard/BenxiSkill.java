package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class BenxiSkill extends SkillItem implements ModTools {
    public BenxiSkill(Settings settings) {super(settings);}
    private int tick = 0;

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
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
            NbtCompound nbt = new NbtCompound();
            if (++tick >= 20) {
                tick = 0;
                if (stack.getNbt() == null) {nbt.putInt("benxi", 0); stack.setNbt(nbt);}
                else {
                    int benxi = stack.getNbt().getInt("benxi");
                    if (benxi > 0) {effectChange(entity, ModItems.REACH, benxi, 20);}
                }
            }
        }
        super.tick(stack, slot, entity);
    }
}
