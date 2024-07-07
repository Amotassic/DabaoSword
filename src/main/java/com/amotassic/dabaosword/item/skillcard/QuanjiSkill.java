package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.voice;

public class QuanjiSkill extends SkillItem {
    public QuanjiSkill(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (stack.getNbt() != null && stack.getNbt().contains("quanji")) {
            int quan = stack.getNbt().getInt("quanji");
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
