package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QuanjiSkill extends SkillItem implements ModTools {
    public QuanjiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.get(ModItems.TAGS) != null) {
            int quan = Objects.requireNonNull(stack.get(ModItems.TAGS));
            tooltip.add(Text.of("权："+quan));
        }
        tooltip.add(Text.translatable("item.dabaosword.quanji.tooltip1").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.dabaosword.quanji.tooltip2").formatted(Formatting.BLUE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && !user.isSneaking()) {
            if (new Random().nextFloat() < 0.5) {voice(user, Sounds.ZILI1);} else {voice(user, Sounds.ZILI2);}
        }
        return super.use(world, user, hand);
    }
    //技能效果实现的代码见监听事件部分
}
