package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class HuojiSkill extends SkillItem implements ModTools {
    private final NbtCompound nbt = new NbtCompound();
    private int cd = nbt.getInt("cooldown");

    public HuojiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+cd/20+"s"));
        tooltip.add(Text.translatable("item.dabaosword.huoji.tooltip").formatted(Formatting.RED));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            ItemStack stack1 = player.getStackInHand(Hand.OFF_HAND);
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.BASIC_CARD)) {
                cd =20 * 15;
                viewAs(player, Tags.Items.BASIC_CARD, ModItems.FIRE_ATTACK, Sounds.HUOJI1, Sounds.HUOJI2);
            }
            if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
        }
        super.tick(stack, slot, entity);
    }
}
