package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LuanjiSkill extends SkillItem implements ModTools {

    public LuanjiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
        tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
        tooltip.add(Text.translatable("item.dabaosword.luanji.tooltip"));
    }

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.CARD) && stack1.getCount() > 1) {
                stack.set(ModItems.CD, 15);
                stack1.decrement(2);
                player.giveItemStack(ModItems.WANJIAN.getDefaultStack());
                float i = new Random().nextFloat();
                if (i < 0.25) {voice(player, Sounds.LUANJI1);} else if (0.25 <= i && i < 0.5) {voice(player, Sounds.LUANJI2);}
                else if (0.5 <= i && i < 0.75) {voice(player, Sounds.LUANJI3);} else {voice(player, Sounds.LUANJI4);}
            }
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; stack.set(ModItems.CD, cd);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
