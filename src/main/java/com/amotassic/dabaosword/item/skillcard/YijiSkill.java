package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class YijiSkill extends SkillItem implements ModTools {
    private final NbtCompound nbt = new NbtCompound();
    private byte enabled = nbt.getByte("enabled");

    public YijiSkill(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (enabled == 1) {
            tooltip.add(Text.translatable("skill.dabaosword.enabled").formatted(Formatting.GREEN));
        } else {tooltip.add(Text.translatable("skill.dabaosword.disabled").formatted(Formatting.RED));}
        tooltip.add(Text.literal("CD: 30s"));
        tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
            if (enabled == 1) {
                player.giveItemStack(new ItemStack(ModItems.WUZHONG));
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 30,0,false,true,true));
                if (new Random().nextFloat() < 0.5) {
                    voice(player, Sounds.YIJI1);
                } else {
                    voice(player, Sounds.YIJI2);
                }
            }
        }
    }
    //右键使用控制是否启用
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (enabled == 1) {enabled = 0;}
            else {enabled = 1;}
            nbt.putByte("enabled", enabled); stack.setNbt(nbt);
        }
        return super.use(world, user, hand);
    }
}
