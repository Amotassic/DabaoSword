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
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Objects;

public class KanpoSkill extends SkillItem implements ModTools {

    public KanpoSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
        tooltip.add(Text.literal(cd == 0 ? "CD: 10s" : "CD: 10s   left: "+ cd +"s"));
        tooltip.add(Text.translatable("item.dabaosword.kanpo.tooltip").formatted(Formatting.RED));
    }

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            int cd = stack.get(ModItems.CD) == null ? 0 : Objects.requireNonNull(stack.get(ModItems.CD));
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.ARMOURY_CARD)) {
                stack.set(ModItems.CD, 10);
                viewAs(player, Tags.Items.ARMOURY_CARD, ModItems.WUXIE, Sounds.KANPO1, Sounds.KANPO2);
            }
            if (++tick >= 20) {
                tick = 0;
                if (cd > 0) {cd--; stack.set(ModItems.CD, cd);}
            }
        }
        super.tick(stack, slot, entity);
    }
}
